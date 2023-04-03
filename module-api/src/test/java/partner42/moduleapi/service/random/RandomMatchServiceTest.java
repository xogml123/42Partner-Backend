package partner42.moduleapi.service.random;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.RandomMatchAlreadyExistException;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.random.RandomMatchSearch;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CustomTimeUtils;


@Slf4j
@ExtendWith(MockitoExtension.class)
class RandomMatchServiceTest {

    @InjectMocks
    private RandomMatchService randomMatchService;
    @Mock
    private RandomMatchRepository randomMatchRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createRandomMatch_whenMatchConditionRandomMatchDtoProper_ThenRandomMatchList()
        throws Exception {
        //given
        User takim = User.of("takim", null, null, null, null, Member.of("takim"));
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        String username = "takim";
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchDto randomMatchDtoEmptyPlace = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of())
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        RandomMatchDto randomMatchDtoEmptyList = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of())
                .build())
            .build();

        RandomMatchDto randomMatchStudyDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .build())
            .build();

        RandomMatchDto randomMatchStudyDtoEmptyList = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of())
                .build())
            .build();

        given(randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            any())).willReturn(List.of());
        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(takim));

        //when
        List<RandomMatch> randomMatches = randomMatchService.createRandomMatch(username,
            randomMatchDto, now);
        List<RandomMatch> randomMatchesWithEmptyPlace = randomMatchService.createRandomMatch(
            username, randomMatchDtoEmptyPlace, now);
        List<RandomMatch> randomMatchesWithEmptyList = randomMatchService.createRandomMatch(
            username, randomMatchDtoEmptyList, now);

        List<RandomMatch> randomMatchesStudy = randomMatchService.createRandomMatch(username,
            randomMatchStudyDto, now);
        List<RandomMatch> randomMatchesStudyWithEmptyList = randomMatchService.createRandomMatch(
            username, randomMatchStudyDtoEmptyList, now);

        //then
        assertThat(randomMatches).hasSize(1);
        //모든 필드와 객체가 적절하게 생성되는지
        assertThat(randomMatches).extracting(RandomMatch::getIsExpired)
                .containsExactly(false);
        assertThat(randomMatches).extracting(RandomMatch::getRandomMatchCondition)
            .extracting(RandomMatchCondition::getPlace, RandomMatchCondition::getContentCategory,
                RandomMatchCondition::getTypeOfStudy, RandomMatchCondition::getWayOfEating)
            .containsExactlyInAnyOrder(
                tuple(Place.GAEPO, ContentCategory.MEAL, null, WayOfEating.DELIVERY)
            );
        //Place가 빈 List인 경우 모든 Place로 RandomMatch반환.
        assertThat(randomMatchesWithEmptyPlace).hasSize(Place.values().length);
        //조건 필드가 빈 List인 경우 모든 조건으로 RandomMatch반환.
        assertThat(randomMatchesWithEmptyList).extracting(RandomMatch::getRandomMatchCondition)
            .extracting(RandomMatchCondition::getWayOfEating)
            .containsOnly(WayOfEating.values());

        //study(다른 ContentCategory)
        assertThat(randomMatchesStudy).extracting(RandomMatch::getIsExpired)
            .containsExactly(false);
        assertThat(randomMatchesStudy).extracting(RandomMatch::getRandomMatchCondition)
            .extracting(RandomMatchCondition::getPlace, RandomMatchCondition::getContentCategory,
                RandomMatchCondition::getTypeOfStudy, RandomMatchCondition::getWayOfEating)
            .containsExactlyInAnyOrder(
                tuple(Place.GAEPO, ContentCategory.STUDY, TypeOfStudy.INNER_CIRCLE, null)
            );
        //조건 필드가 빈 List인 경우 모든 조건으로 RandomMatch반환.
        assertThat(randomMatchesStudyWithEmptyList).extracting(RandomMatch::getRandomMatchCondition)
            .extracting(RandomMatchCondition::getTypeOfStudy)
            .containsOnly(TypeOfStudy.values());
    }

    @Test
    void createRandomMatch_whenUsernameNotExist_ThenThrowException() throws Exception {
        //given
        User takim = User.of("takim", null, null, null, null, Member.of("takim"));

        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        String notExistUsername = "notExistUsername";

        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(takim));
        given(userRepository.findByUsername(notExistUsername)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(notExistUsername, randomMatchDto, now))
            .isInstanceOf(NoEntityException.class);
    }

    @Test
    void createRandomMatch_whenSameUserAlreadyApplySameCategoryRandomMatch_ThenException()
        throws Exception {
        //given
        Member takimMember = Member.of("takim");
        User takim = User.of("takim", null, null, null, null, takimMember);
        takimMember.setId(1L);
        Member unknownMember = Member.of("unknown");
        User unknown = User.of("unknown", null, null, null, null, unknownMember);
        unknownMember.setId(2L);

        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchDto randomMatchDtoEmptyList = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of())
                .build())
            .build();

        RandomMatchDto randomMatchStudyDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .build())
            .build();

        RandomMatchDto randomMatchStudyDtoEmptyList = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of())
                .build())
            .build();

        given(
            randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
                RandomMatchSearch.builder()
                    .contentCategory(ContentCategory.MEAL)
                    .memberId(takimMember.getId())
                    .isExpired(false)
                    .createdAt(now.minusMinutes(30))
                    .build()))
            .willReturn(List.of()).willReturn(
                List.of(RandomMatch.of(null, null)));

        given(
            randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
                RandomMatchSearch.builder()
                    .contentCategory(ContentCategory.STUDY)
                    .memberId(takimMember.getId())
                    .isExpired(false)
                    .createdAt(now.minusMinutes(30))
                    .build()))
            .willReturn(List.of()).willReturn(
                List.of(RandomMatch.of(null, null)));
        given(
            randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
                RandomMatchSearch.builder()
                    .contentCategory(ContentCategory.MEAL)
                    .memberId(unknownMember.getId())
                    .isExpired(false)
                    .createdAt(now.minusMinutes(30))
                    .build()))
            .willReturn(List.of()).willReturn(
                List.of(RandomMatch.of(null, null)));

        given(userRepository.findByUsername("takim")).willReturn(Optional.ofNullable(takim));
        given(userRepository.findByUsername("unknown")).willReturn(Optional.ofNullable(unknown));

        //when

        List<RandomMatch> randomMatches = randomMatchService.createRandomMatch(takim.getUsername(),
            randomMatchDto, now);

        //then
        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(takim.getUsername(), randomMatchDto, now))
            .isInstanceOf(RandomMatchAlreadyExistException.class);
        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(takim.getUsername(), randomMatchDtoEmptyList, now))
            .isInstanceOf(RandomMatchAlreadyExistException.class);

        assertThatNoException().isThrownBy(() -> randomMatchService.createRandomMatch(
            takim.getUsername(),
            randomMatchStudyDto, now));

        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(takim.getUsername(),
                randomMatchStudyDto, now))
            .isInstanceOf(RandomMatchAlreadyExistException.class);

        assertThatNoException().isThrownBy(() ->
            randomMatchService.createRandomMatch(unknown.getUsername(),
                randomMatchDto, now));

        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(unknown.getUsername(),
                randomMatchDto, now)
        )
            .isInstanceOf(RandomMatchAlreadyExistException.class);
    }

}