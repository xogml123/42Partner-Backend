package partner42.moduleapi.service.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.config.kafka.AlarmEvent;
import partner42.moduleapi.dto.random.RandomMatchCancelRequest;
import partner42.moduleapi.dto.random.RandomMatchCountResponse;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDtoFactory;
import partner42.moduleapi.dto.random.RandomMatchExistDto;
import partner42.moduleapi.dto.random.RandomMatchParam;
import partner42.moduleapi.producer.random.MatchMakingEvent;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.random.RandomMatchBulkUpdateDto;
import partner42.modulecommon.repository.random.RandomMatchConditionSearch;
import partner42.modulecommon.repository.random.RandomMatchSearch;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.RandomMatchAlreadyExistException;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.user.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RandomMatchService {
    private static final String SYSTEM = "system";

    private final UserRepository userRepository;
    private final RandomMatchRepository randomMatchRepository;
    private final MatchMemberRepository matchMemberRepository;
    private final MatchRepository matchRepository;
    private final MatchConditionRepository matchConditionRepository;
    private final MatchConditionMatchRepository matchConditionMatchRepository;
    private final RandomMatchDtoFactory randomMatchDtoFactory;

    /**
     * DB와 상관없이 DAO를 Mocking하는 테스트를 구행해보기 위해서 List<RandomMatch>라는 Entity자체를 return 하도록 구성하였다. 하지만
     * Controller로 Entity자체를 반환하는 형태는 좋은 방식이 아니며 CQRS관점에서도 좋지 않은 방식이다.
     *
     * @param username
     * @param randomMatchDto
     * @param now
     * @return
     */
    @Transactional
    public List<RandomMatch> createRandomMatch(String username,
        RandomMatchDto randomMatchDto, LocalDateTime now) {
        Member member = getUserByUsernameOrException(username).getMember();
        //"2020-12-01T00:00:00"
        //이미 RandomMatch.MAX_WAITING_TIME분 이내에 랜덤 매칭 신청을 한 경우 인지 체크
        verifyAlreadyApplied(randomMatchDto.getContentCategory(), member, now);

        //요청 dto로 부터 랜덤 매칭 모든 경우의 수 만들어서 RandomMatch 여러개로 변환
        List<RandomMatch> randomMatches = randomMatchDto.makeAllAvailRandomMatchesFromRandomMatchDto(
            member);

        //랜덤 매칭 신청한 것 DB에 기록.
        randomMatchRepository.saveAll(randomMatches);
        return randomMatches;
    }


    @Transactional
    public void deleteRandomMatch(String username,
        RandomMatchCancelRequest request, LocalDateTime now) {
        Long memberId = getUserByUsernameOrException(username).getMember().getId();
        //생성된 지 RandomMatch.MAX_WAITING_TIME분 이내 + 취소되지 않은 신청 내역 있는지 확인.
        //매칭 맺어주는 알고리즘과 겹칩을 방지하기 위해서 lock을 걸어야함.
        List<RandomMatch> randomMatches = randomMatchRepository.findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(request.getContentCategory())
                .memberId(memberId)
                .isExpired(false)
                .createdAt(RandomMatch.getValidTime(now))
                .build());
        // 활성화된 randomMatch가 db에 없으면 취소할 매치가 없는 경우
        if (randomMatches.isEmpty()) {
            throw new InvalidInputException(ErrorCode.ALREADY_CANCELED_RANDOM_MATCH);
        }
        randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(randomMatches.stream()
            .map(rm -> RandomMatchBulkUpdateDto.builder()
                .id(rm.getId())
                .version(rm.getVersion())
                .build())
            .collect(Collectors.toSet()));
    }

    public RandomMatchExistDto checkRandomMatchExist(String username,
        RandomMatchParam randomMatchCancelRequest, LocalDateTime now) {
        Member member = getUserByUsernameOrException(username).getMember();
        try {
            verifyAlreadyApplied(randomMatchCancelRequest.getContentCategory(), member,
                now);
            return RandomMatchExistDto.builder()
                .isExist(false).build();
        } catch (RandomMatchAlreadyExistException e) {
            return RandomMatchExistDto.builder()
                .isExist(true).build();
        }
    }

    public RandomMatchDto readRandomMatchCondition(String username,
        RandomMatchParam randomMatchCancelRequest, LocalDateTime now) {

        Member member = getUserByUsernameOrException(username).getMember();
        Long memberId = member.getId();

        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(randomMatchCancelRequest.getContentCategory())
                .memberId(memberId)
                .isExpired(false)
                .createdAt(RandomMatch.getValidTime(now))
                .build());
        return randomMatchDtoFactory.createRandomMatchDto(
            randomMatchCancelRequest.getContentCategory(), randomMatches);
    }

    public RandomMatchCountResponse countMemberOfRandomMatchNotExpire(
        RandomMatchParam randomMatchCancelRequest, LocalDateTime now) {

        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(randomMatchCancelRequest.getContentCategory())
                .isExpired(false)
                .createdAt(RandomMatch.getValidTime(now))
                .build());

        int randomMatchParticipantCount = randomMatches.stream()
            .map(RandomMatch::getMember)
            .collect(Collectors.toSet())
            .size();

        return RandomMatchCountResponse.builder()
            .randomMatchCount(randomMatchParticipantCount)
            .build();
    }

    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }

    private void verifyAlreadyApplied(ContentCategory contentCategory, Member member,
        LocalDateTime now) {
        LocalDateTime validTime = RandomMatch.getValidTime(now);
        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(contentCategory)
                .memberId(member.getId())
                .isExpired(false)
                .createdAt(validTime)
                .build());
        if (!randomMatches.isEmpty()) {
            throw new RandomMatchAlreadyExistException(ErrorCode.RANDOM_MATCH_ALREADY_EXIST);
        }
    }

    /**
     * 테스트 케이스 1. 여러 조건이 들어오는 경우 매칭이 하나의 조건으로 채결되면 다른 신청 무효화 - 안됨 2. Meal 만 매칭이 가능. 3. 만료시간 제대로 되는지
     * 확인. 4. 매칭 조건 다른 것들 섞여서 생성해도 잘 되는지 체크 5. 같은 조건 먼저 신청한사람이 먼저 매칭되도록
     *
     */
    public List<RandomMatch> getValidRandomMatchesSortedByMatchCondition(
        MatchMakingEvent matchMakingEvent) {
        LocalDateTime matchAvailableApplyTime = matchMakingEvent.getNow()
            .minusMinutes(RandomMatch.MAX_WAITING_TIME);
        return randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndRandomMatchConditionAndSortedByRandomMatchConditionAndCreatedAtASC(
            matchAvailableApplyTime,
            false,
        RandomMatchConditionSearch.builder()
                .contentCategory(matchMakingEvent.getContentCategory())
                .placeList(matchMakingEvent.getPlaceList())
                .wayOfEatingList(matchMakingEvent.getWayOfEatingList())
                .typeOfStudyList(matchMakingEvent.getTypeOfStudyList())
                .build());
    }

    /**
     * matchedRandomMatches는 같은 RandomMatchCondition을 가지고 있어야함.
     *
     * @param matchedRandomMatches
     * @param now
     * @return
     */
    @Transactional
    public Match makeMatchInRDB(List<RandomMatch> matchedRandomMatches, LocalDateTime now) {

        Match match = createAndSaveMatch(matchedRandomMatches);
        //RDB에 Match에 저장, MatchMember저장
        //matchMember
        createAndSaveMatchMembers(match, matchedRandomMatches);
        //matchCondition
        createAndSaveMatchCondition(match, matchedRandomMatches);

        //RandomMatch에 isExpired = true로 업데이트
        randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(
            matchedRandomMatches.stream()
                .map(randomMatch -> RandomMatchBulkUpdateDto.builder()
                    .id(randomMatch.getId())
                    .version(randomMatch.getVersion())
                    .build())
                .collect(Collectors.toSet()));

        // OSIV 옵션 때문에 이 메서드를 호출한 외부에서 Lazy loading할 수 없어 미리 호출,
        // 다른 방법 생각해봐야함.
        List<String> emails = match.getMatchMembers().stream()
            .map(MatchMember::getMember)
            .map(Member::getUser)
            .map(User::getEmail)
            .collect(Collectors.toList());
        List<AlarmEvent> alarmEvents = match.getMatchMembers().stream()
            .map(MatchMember::getMember)
            .map(member -> new AlarmEvent(AlarmType.MATCH_CONFIRMED,
                AlarmArgs.builder()
                    .opinionId(null)
                    .articleId(null)
                    .callingMemberNickname(SYSTEM)
                    .build(), member.getId(), SseEventName.ALARM_LIST))
            .collect(Collectors.toList());
        return match;
    }

    private Match createAndSaveMatch(List<RandomMatch> matchedRandomMatches) {
        Match match = Match.of(MatchStatus.MATCHED,
            matchedRandomMatches.get(0).getRandomMatchCondition().getContentCategory(),
            MethodCategory.RANDOM, null, RandomMatch.MATCH_COUNT);
        return matchRepository.save(match);
    }

    private void createAndSaveMatchMembers(Match match, List<RandomMatch> matchedRandomMatches) {
        matchedRandomMatches.stream()
            .map(RandomMatch::getMember)
            .forEach(member -> {
                matchMemberRepository.save(MatchMember.of(match, member, false));
            });
    }

    private List<MatchCondition> createAndSaveMatchCondition(Match match,
        List<RandomMatch> matchedRandomMatches) {
        RandomMatch randomMatch = matchedRandomMatches.get(0);
        List<MatchCondition> matchConditions = new ArrayList<>();
        String errorMessage = "MatchCondition이 존재하지 않습니다. value : ";
        matchConditions.add(matchConditionRepository.findByValue(
                randomMatch.getRandomMatchCondition().getPlace().toString())
            .orElseThrow(() ->
                new IllegalStateException(
                    errorMessage + randomMatch.getRandomMatchCondition().getPlace().toString())));
        if (randomMatch.getRandomMatchCondition().getWayOfEating() != null) {
            matchConditions.add(matchConditionRepository.findByValue(
                    randomMatch.getRandomMatchCondition().getWayOfEating().toString())
                .orElseThrow(() ->
                    new IllegalStateException(errorMessage
                        + (randomMatch).getRandomMatchCondition().getWayOfEating().toString())));
        }
        if (randomMatch.getRandomMatchCondition().getTypeOfStudy() != null) {
            matchConditions.add(matchConditionRepository.findByValue(
                    randomMatch.getRandomMatchCondition().getTypeOfStudy().toString())
                .orElseThrow(() ->
                    new IllegalStateException(errorMessage
                        + (randomMatch).getRandomMatchCondition().getTypeOfStudy().toString())));
        }
        matchConditionMatchRepository.saveAll(matchConditions.stream()
            .map((matchCondition) ->
                MatchConditionMatch.of(match, matchCondition)
            )
            .collect(Collectors.toList()));
        return matchConditions;
    }

}
