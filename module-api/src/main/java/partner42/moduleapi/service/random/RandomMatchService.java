package partner42.moduleapi.service.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto.MatchConditionRandomMatchDtoBuilder;
import partner42.moduleapi.dto.random.RandomMatchCancelRequest;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchExistDto;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.MealRandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.StudyRandomMatch;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.RandomMatchAlreadyExistException;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.redis.RedisTransactionUtil;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RandomMatchService {

    private final UserRepository userRepository;
    private final RandomMatchRepository randomMatchRepository;


    @Transactional
    public void createRandomMatch(String username,
        RandomMatchDto randomMatchDto) {
        Member member = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND)).getMember();
        //"2020-12-01T00:00:00"
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        //이미 RandomMatch.MAX_WAITING_TIME분 이내에 랜덤 매칭 신청을 한 경우 인지 체크
        verifyAlreadyAppliedPessimisticWriteLock(randomMatchDto.getContentCategory(), member, now);

        //요청 dto로 부터 랜덤 매칭 모든 경우의 수 만들어서 RandomMatch 여러개로 변환
        List<RandomMatch> randomMatches = makeAllAvailRandomMatchesFromRandomMatchDto(
            randomMatchDto, member, now);

        //랜덤 매칭 신청한 것 DB에 기록.
        randomMatchRepository.saveAll(randomMatches);
        // 여러 매칭 조건들 redis 에 저장.
//        /**
//         * redis 트랜잭션이 종료됨과 동시에 mysql커넥션이 종료된다면
//         * redis에서 삭제되었지만 mysql에서 삭제되지 않은 상황이 발생할 수 있다.
//         * 2PhaseCommit으로 해결해보려고 했지만, redis는 2PhaseCommit을 지원하지 않는다.
//         * EventQueue같은 것들을 활용하여 해결해볼 계획.
//         */
//        redisTransactionUtil.wrapTransaction(() -> {
//                randomMatches.forEach(randomMatch ->
//                    randomMatchRedisRepository.addToSortedSet(randomMatch.toKey(),
//                        randomMatch.toValue(), 0.0)
//                );
//            }
//        );
    }


    private void verifyAlreadyAppliedPessimisticWriteLock(ContentCategory contentCategory, Member member,
        LocalDateTime now) {
        if ((contentCategory.equals(ContentCategory.MEAL) &&
            randomMatchRepository.findMealPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
                now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                member.getId(), false).size() > 0) ||
            (contentCategory.equals(ContentCategory.STUDY) &&
                randomMatchRepository.findStudyPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
                    now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                    member.getId(), false).size() > 0)) {

            throw new RandomMatchAlreadyExistException(ErrorCode.RANDOM_MATCH_ALREADY_EXIST);
        }
    }

    private void verifyAlreadyApplied(ContentCategory contentCategory, Member member,
        LocalDateTime now) {
        if ((contentCategory.equals(ContentCategory.MEAL) &&
            randomMatchRepository.findMealByCreatedAtBeforeAndIsExpiredAndMemberId(
                now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                member.getId(), false).size() > 0) ||
            (contentCategory.equals(ContentCategory.STUDY) &&
                randomMatchRepository.findStudyByCreatedAtBeforeAndIsExpiredAndMemberId(
                    now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                    member.getId(), false).size() > 0)) {

            throw new RandomMatchAlreadyExistException(ErrorCode.RANDOM_MATCH_ALREADY_EXIST);
        }
    }

    @Transactional
    public void deleteRandomMatch(String username,
        RandomMatchCancelRequest request) {
        Long memberId = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND)).getMember().getId();
        List<RandomMatch> randomMatches = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        //생성된 지 RandomMatch.MAX_WAITING_TIME분 이내 + 취소되지 않은 신청 내역 있는지 확인.
        ContentCategory contentCategory = request.getContentCategory();
        if (contentCategory == ContentCategory.MEAL) {
            randomMatches.addAll(
                randomMatchRepository.findMealPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
                    now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                    memberId, false));
        } else if (contentCategory == ContentCategory.STUDY) {
            randomMatches.addAll(
                randomMatchRepository.findStudyPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
                    now.minusMinutes(RandomMatch.MAX_WAITING_TIME),
                    memberId, false));
        }
        // 활성화된 randomMatch가 db에 없으면 취소할 매치가 없는 경우 exception
        if (randomMatches.isEmpty()) {
            throw new InvalidInputException(ErrorCode.ALREADY_CANCELED_RANDOM_MATCH);
        }

        //db상에서 만료
        randomMatches
            .forEach(RandomMatch::expire);

//        //redis 모두 삭제시도
//        /**
//         * redis 트랜잭션이 종료됨과 동시에 mysql커넥션이 종료된다면
//         * redis에는 생성되었지만 mysql에서는 생성되지 않는 상황이 발생할 수 있다.
//         * 2PhaseCommit으로 해결해보려고 했지만, redis는 2PhaseCommit을 지원하지 않는다.
//         * eventQueue같은 것들을 나중에 활용하여 해결해볼 계획입니다.
//         */
//        redisTransactionUtil.wrapTransaction(() -> {
//                randomMatches.forEach(randomMatch ->
//                    randomMatchRedisRepository.deleteSortedSet(randomMatch.toKey(),
//                        randomMatch.toValue()));
//            }
//        );
    }


    /**
     * 요청 dto로 부터 랜덤 매칭 모든 경우의 수 만들어서 RandomMatch 여러개로 변환
     *
     * @param randomMatchDto
     * @return
     */
    private List<RandomMatch> makeAllAvailRandomMatchesFromRandomMatchDto(
        RandomMatchDto randomMatchDto, Member member, LocalDateTime now) {
        //아무 matchCondition필드에 값이 없는 경우 모든 조건으로 변환.

        List<RandomMatch> randomMatches = new ArrayList<>();
        MatchConditionRandomMatchDto matchConditionRandomMatchDto = randomMatchDto.getMatchConditionRandomMatchDto();
        if (randomMatchDto.getContentCategory().equals(ContentCategory.STUDY) &&
            matchConditionRandomMatchDto.getTypeOfStudyList().isEmpty()) {
            matchConditionRandomMatchDto.getTypeOfStudyList()
                .addAll(List.of(TypeOfStudy.values()));
        } else if (randomMatchDto.getContentCategory().equals(ContentCategory.MEAL) &&
            matchConditionRandomMatchDto.getWayOfEatingList().isEmpty()) {
            matchConditionRandomMatchDto.getWayOfEatingList()
                .addAll(List.of(WayOfEating.values()));
        }
        // redis 조건에 따라 여러 데이터 생성
        for (Place place : matchConditionRandomMatchDto.getPlaceList()) {
            if (randomMatchDto.getContentCategory().equals(ContentCategory.STUDY)) {
                for (TypeOfStudy typeOfStudy : matchConditionRandomMatchDto.getTypeOfStudyList()) {
                    randomMatches.add(new StudyRandomMatch(ContentCategory.STUDY,
                        place, member, typeOfStudy));
                }
            } else if (randomMatchDto.getContentCategory().equals(ContentCategory.MEAL)) {
                for (WayOfEating wayOfEating : matchConditionRandomMatchDto.getWayOfEatingList()) {
                    randomMatches.add(new MealRandomMatch(ContentCategory.MEAL,
                        place, member, wayOfEating));
                }
            }
        }
        return randomMatches;
    }


    public RandomMatchExistDto checkRandomMatchExist(String username,
        RandomMatchCancelRequest randomMatchCancelRequest) {
        Member member = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND)).getMember();
        try {
            verifyAlreadyApplied(randomMatchCancelRequest.getContentCategory(), member,
                LocalDateTime.now());
            return RandomMatchExistDto.builder()
                .isExist(false).build();
        } catch (RandomMatchAlreadyExistException e) {
            return RandomMatchExistDto.builder()
                .isExist(true).build();
        }
    }

    public RandomMatchDto readRandomMatchCondition(String username,
        RandomMatchCancelRequest randomMatchCancelRequest) {

        Member member = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND)).getMember();
        Long memberId = member.getId();
        LocalDateTime now = LocalDateTime.now();
        List<RandomMatch> randomMatches = new ArrayList<>();
        MatchConditionRandomMatchDtoBuilder builder = MatchConditionRandomMatchDto.builder();
        if (randomMatchCancelRequest.getContentCategory() == ContentCategory.MEAL) {
            randomMatches = randomMatchRepository.findMealByCreatedAtBeforeAndIsExpiredAndMemberId(
                now.minusMinutes(RandomMatch.MAX_WAITING_TIME), memberId, false);
            builder = builder.wayOfEatingList(new ArrayList<>(randomMatches.stream()
                .map(randomMatch -> ((MealRandomMatch) randomMatch).getWayOfEating())
                .collect(Collectors.toSet())));
        } else if (randomMatchCancelRequest.getContentCategory() == ContentCategory.STUDY) {
            randomMatches = randomMatchRepository.findStudyByCreatedAtBeforeAndIsExpiredAndMemberId(
                now.minusMinutes(RandomMatch.MAX_WAITING_TIME), memberId, false);
            builder = builder.typeOfStudyList(new ArrayList<>(randomMatches.stream()
                .map(randomMatch -> ((StudyRandomMatch) randomMatch).getTypeOfStudy())
                .collect(Collectors.toSet())));
        }
        MatchConditionRandomMatchDto matchConditionRandomMatchDto = builder.placeList(
                new ArrayList<>(randomMatches.stream()
                    .map(RandomMatch::getPlace)
                    .collect(Collectors.toSet())))
            .build();
        return RandomMatchDto.builder()
            .contentCategory(randomMatchCancelRequest.getContentCategory())
            .matchConditionRandomMatchDto(matchConditionRandomMatchDto)
            .build();
    }
}
