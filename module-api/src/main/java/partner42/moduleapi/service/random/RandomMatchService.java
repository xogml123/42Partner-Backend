package partner42.moduleapi.service.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchCancelRequest;
import partner42.moduleapi.dto.random.RandomMatchCountResponse;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchExistDto;
import partner42.moduleapi.dto.random.RandomMatchParam;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.random.RandomMatchBulkUpdateDto;
import partner42.modulecommon.repository.random.RandomMatchSearch;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
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

    private final UserRepository userRepository;
    private final RandomMatchRepository randomMatchRepository;


    @Transactional
    public void createRandomMatch(String username,
        RandomMatchDto randomMatchDto) {
        Member member = getUserByUsernameOrException(username).getMember();
        //"2020-12-01T00:00:00"
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        //이미 RandomMatch.MAX_WAITING_TIME분 이내에 랜덤 매칭 신청을 한 경우 인지 체크
        verifyAlreadyApplied(randomMatchDto.getContentCategory(), member, now);

        //요청 dto로 부터 랜덤 매칭 모든 경우의 수 만들어서 RandomMatch 여러개로 변환
        List<RandomMatch> randomMatches = makeAllAvailRandomMatchesFromRandomMatchDto(
            randomMatchDto, member, now);

        //랜덤 매칭 신청한 것 DB에 기록.
        randomMatchRepository.saveAll(randomMatches);
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

    @Transactional
    public void deleteRandomMatch(String username,
        RandomMatchCancelRequest request) {
        Long memberId = getUserByUsernameOrException(username).getMember().getId();
        LocalDateTime now = LocalDateTime.now();
        //생성된 지 RandomMatch.MAX_WAITING_TIME분 이내 + 취소되지 않은 신청 내역 있는지 확인.
        //취소 하는 도중 매칭이 잡힐 경우를 대비하여 for update
        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
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

        //변경 감지 이용하는 경우 성능 저하
//        randomMatches
//            .forEach(RandomMatch::expire);
        randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(randomMatches.stream()
            .map(rm -> RandomMatchBulkUpdateDto.builder()
                .id(rm.getId())
                .version(rm.getVersion())
                .build())
            .collect(Collectors.toSet()));
    }

    public RandomMatchExistDto checkRandomMatchExist(String username,
        RandomMatchParam randomMatchCancelRequest) {
        Member member = getUserByUsernameOrException(username).getMember();
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
        RandomMatchParam randomMatchCancelRequest) {

        Member member = getUserByUsernameOrException(username).getMember();
        Long memberId = member.getId();
        LocalDateTime now = LocalDateTime.now();

        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(randomMatchCancelRequest.getContentCategory())
                .memberId(memberId)
                .isExpired(false)
                .createdAt(RandomMatch.getValidTime(now))
                .build());

        Set<WayOfEating> wayOfEatings = randomMatches.stream()
            .map(RandomMatch::getRandomMatchCondition)
            .map(RandomMatchCondition::getWayOfEating)
            .collect(Collectors.toSet());

        Set<TypeOfStudy> typeOfStudies = randomMatches.stream()
            .map(RandomMatch::getRandomMatchCondition)
            .map(RandomMatchCondition::getTypeOfStudy)
            .collect(Collectors.toSet());

        return RandomMatchDto.builder()
            .contentCategory(randomMatchCancelRequest.getContentCategory())
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(new ArrayList<>(randomMatches.stream()
                    .map(RandomMatch::getRandomMatchCondition)
                    .map(RandomMatchCondition::getPlace)
                    .collect(Collectors.toSet())))
                .wayOfEatingList(new ArrayList<>(wayOfEatings))
                .typeOfStudyList(new ArrayList<>(typeOfStudies))
                .build())
            .build();
    }

    public RandomMatchCountResponse countRandomMatchNotExpired(
        RandomMatchParam randomMatchCancelRequest) {
        LocalDateTime now = LocalDateTime.now();

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
        //matchConditionRandomMatchDto의 필드가 비어있는 경우 모든 조건으로 변환
        if (randomMatchDto.getContentCategory().equals(ContentCategory.STUDY) &&
            matchConditionRandomMatchDto.getTypeOfStudyList().isEmpty()) {
            matchConditionRandomMatchDto.getTypeOfStudyList()
                .addAll(List.of(TypeOfStudy.values()));
        } else if (randomMatchDto.getContentCategory().equals(ContentCategory.MEAL) &&
            matchConditionRandomMatchDto.getWayOfEatingList().isEmpty()) {
            matchConditionRandomMatchDto.getWayOfEatingList()
                .addAll(List.of(WayOfEating.values()));
        }
        // 조건에 따라 모든 경우의 수 RandomMatch 생성
        for (Place place : matchConditionRandomMatchDto.getPlaceList()) {
            if (randomMatchDto.getContentCategory().equals(ContentCategory.STUDY)) {
                for (TypeOfStudy typeOfStudy : matchConditionRandomMatchDto.getTypeOfStudyList()) {

                    randomMatches.add(
                        RandomMatch.of(RandomMatchCondition.of(randomMatchDto.getContentCategory(),
                            place, typeOfStudy), member));
                }
            } else if (randomMatchDto.getContentCategory().equals(ContentCategory.MEAL)) {
                for (WayOfEating wayOfEating : matchConditionRandomMatchDto.getWayOfEatingList()) {

                    randomMatches.add(
                        RandomMatch.of(RandomMatchCondition.of(randomMatchDto.getContentCategory(),
                            place, wayOfEating), member));
                }
            }
        }
        return randomMatches;
    }

}
