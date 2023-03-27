package partner42.moduleapi.service.random;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import partner42.moduleapi.TestBootstrapConfig;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchCancelRequest;
import partner42.moduleapi.dto.random.RandomMatchCountResponse;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchExistDto;
import partner42.moduleapi.dto.random.RandomMatchParam;
import partner42.moduleapi.service.activity.ActivityService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.RandomMatchAlreadyExistException;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CustomTimeUtils;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RandomMatchService.class,
    Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class,
})
class RandomMatchServiceWithDAOTest {

    @Autowired
    private RandomMatchService randomMatchService;

    @Autowired
    private BootstrapDataLoader bootstrapDataLoader;

    @Autowired
    private RandomMatchRepository randomMatchRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }


    @Test
    void createRandomMatch_whenMatchConditionRandomMatchDtoProper_ThenRandomMatchList() {
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

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

        //then
        assertThatNoException().isThrownBy(() ->
            randomMatchService.createRandomMatch(takim.getUsername(),
                randomMatchDto, now));
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
            randomMatchService.createRandomMatch(sorkim.getUsername(),
                randomMatchDto, now));
        assertThatThrownBy(() ->
            randomMatchService.createRandomMatch(sorkim.getUsername(),
                randomMatchDto, now)
        ).isInstanceOf(RandomMatchAlreadyExistException.class);
    }

    @Test
    void deleteRandomMatch_whenRandomMatchExist_ThenExpire() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchDto randomMatchStudyDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .build())
            .build();

        RandomMatchCancelRequest randomMatchMeal = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        RandomMatchCancelRequest randomMatchStudy = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.STUDY)
            .build();

        //when
        List<RandomMatch> randomMatchTakimMeal = randomMatchService.createRandomMatch(
            takim.getUsername(),
            randomMatchDto, now);
        List<RandomMatch> randomMatchTakimStudy = randomMatchService.createRandomMatch(
            takim.getUsername(), randomMatchStudyDto, now);
        List<RandomMatch> randomMatchSorkimMeal = randomMatchService.createRandomMatch(
            sorkim.getUsername(), randomMatchDto, now);

        randomMatchService.deleteRandomMatch(takim.getUsername(), randomMatchMeal, now);
        randomMatchService.deleteRandomMatch(takim.getUsername(), randomMatchStudy, now);
        randomMatchService.deleteRandomMatch(sorkim.getUsername(), randomMatchMeal, now);

        List<RandomMatch> randomMatchTakimMealDelete = randomMatchRepository.findAllById(
            randomMatchTakimMeal.stream()
                .map(RandomMatch::getId)
                .collect(Collectors.toList()));
        List<RandomMatch> randomMatchTakimStudyDelete = randomMatchRepository.findAllById(
            randomMatchTakimMeal.stream()
                .map(RandomMatch::getId)
                .collect(Collectors.toList()));
        List<RandomMatch> randomMatchSorkimMealDelete = randomMatchRepository.findAllById(
            randomMatchTakimMeal.stream()
                .map(RandomMatch::getId)
                .collect(Collectors.toList()));
        //then
        assertThat(randomMatchTakimMealDelete).extracting(RandomMatch::getIsExpired)
            .containsOnly(true);
        assertThat(randomMatchTakimStudyDelete).extracting(RandomMatch::getIsExpired)
            .containsOnly(true);
        assertThat(randomMatchSorkimMealDelete).extracting(RandomMatch::getIsExpired)
            .containsOnly(true);

    }

    @Test
    void deleteRandomMatch_whenRandomMatchNotExist_whenDelete_ThenThrow() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        RandomMatchCancelRequest randomMatchMeal = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        RandomMatchCancelRequest randomMatchStudy = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.STUDY)
            .build();

        //when
        assertThatThrownBy(() ->
            randomMatchService.deleteRandomMatch(takim.getUsername(), randomMatchMeal, now))
            .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void checkRandomMatchExist_whenRandomMatchExistOrNot_thenReturn() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchParam randomMatchMeal = RandomMatchParam.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        RandomMatchParam randomMatchStudy = RandomMatchParam.builder()
            .contentCategory(ContentCategory.STUDY)
            .build();

        //when
        randomMatchService.createRandomMatch(takim.getUsername(),
            randomMatchDto, now);
        RandomMatchExistDto randomMatchExistDtoMeal = randomMatchService.checkRandomMatchExist(
            takim.getUsername(), randomMatchMeal, now);
        RandomMatchExistDto randomMatchExistDtoStudy = randomMatchService.checkRandomMatchExist(
            takim.getUsername(), randomMatchStudy, now);
        //then

        assertThat(randomMatchExistDtoMeal.getIsExist()).isTrue();
        assertThat(randomMatchExistDtoStudy.getIsExist()).isFalse();
    }

    @Test
    void readRandomMatchCondition_whenRandomMatchExist_thenReturn() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchParam randomMatchMeal = RandomMatchParam.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        RandomMatchParam randomMatchStudy = RandomMatchParam.builder()
            .contentCategory(ContentCategory.STUDY)
            .build();
        //when
        randomMatchService.createRandomMatch(takim.getUsername(),
            randomMatchDto, now);
        RandomMatchDto randomMatchDto1 = randomMatchService.readRandomMatchCondition(
            takim.getUsername(), randomMatchMeal, now);
        RandomMatchDto randomMatchDto2 = randomMatchService.readRandomMatchCondition(
            takim.getUsername(), randomMatchStudy, now);
        //then
        assertThat(randomMatchDto1).usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(RandomMatchDto.builder()
                .contentCategory(ContentCategory.MEAL)
                .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                    .placeList(List.of(Place.GAEPO))
                    .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                    .build())
                .build());

        assertThat(randomMatchDto2).usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(RandomMatchDto.builder()
                .contentCategory(ContentCategory.STUDY)
                .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                    .build())
                .build());
    }

    @Test
    public void countMemberOfRandomMatchNotExpire_whenUserParticipateRandomMatch_then(){
        //given
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
                .build())
            .build();

        RandomMatchDto randomMatchStudyDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .build())
            .build();

        RandomMatchParam randomMatchMeal = RandomMatchParam.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        RandomMatchParam randomMatchStudy = RandomMatchParam.builder()
            .contentCategory(ContentCategory.STUDY)
            .build();
        //when
        randomMatchService.createRandomMatch(takim.getUsername(),
            randomMatchDto, now);
        randomMatchService.createRandomMatch(takim.getUsername(),
            randomMatchStudyDto, now);

        randomMatchService.createRandomMatch(sorkim.getUsername(),
            randomMatchDto, now);
        randomMatchService.createRandomMatch(hyenam.getUsername(),
            randomMatchDto, now);

        RandomMatchCountResponse randomMatchCountResponseMeal = randomMatchService.countMemberOfRandomMatchNotExpire(
            randomMatchMeal, now);
        RandomMatchCountResponse randomMatchCountResponseStudy = randomMatchService.countMemberOfRandomMatchNotExpire(
            randomMatchStudy, now);

        //then
        assertThat(randomMatchCountResponseMeal.getRandomMatchCount()).isEqualTo(3);
        assertThat(randomMatchCountResponseStudy.getRandomMatchCount()).isEqualTo(1);
    }

}