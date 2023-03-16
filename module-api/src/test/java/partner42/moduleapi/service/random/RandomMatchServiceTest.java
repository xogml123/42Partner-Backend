package partner42.moduleapi.service.random;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.utils.WorkerWithCountDownLatch;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.user.UserRepository;


@Slf4j
@ExtendWith(SpringExtension.class)
@Import(RandomMatchService.class)
class RandomMatchServiceTest {

    @Autowired
    private RandomMatchService randomMatchService;
    @MockBean
    private RandomMatchRepository randomMatchRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User takim = User.of("takim", null, null, null, null, Member.of("takim"));

        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(takim));
    }
//    @Test
//    void createRandomMatch() throws Exception {
//
//        //given
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//
//        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
//            .contentCategory(ContentCategory.MEAL)
//            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
//                .placeList(List.of(Place.GAEPO))
//                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
//                .build())
//            .build();
//
//        //when
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        WorkerWithCountDownLatch sorkimParticipate = new WorkerWithCountDownLatch(
//            "sorkim participate", countDownLatch, () ->
//        {
//
//            randomMatchService.createRandomMatch("sorkim@student.42Seoul.kr", randomMatchDto);
//
//        });
//
//        WorkerWithCountDownLatch sorkimParticipate2 = new WorkerWithCountDownLatch(
//            "sorkim participate2", countDownLatch, () ->
//        {
//
//            randomMatchService.createRandomMatch("sorkim@student.42Seoul.kr", randomMatchDto);
//
//        });
//        sorkimParticipate.start();
//        sorkimParticipate2.start();
//
//        Thread.sleep(10);
//        log.info("-----------------------------------------------");
//        log.info(" Now release the latch:");
//        log.info("-----------------------------------------------");
//        countDownLatch.countDown();
//        Thread.sleep(2000);
//        //then
//    }

    @Test
    void createRandomMatch_when() throws Exception{
        //given

        String username = "takim";
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        //when
        List<RandomMatch> randomMatches = randomMatchService.createRandomMatch("takim", randomMatchDto);
        //then
        assertThat(randomMatches).usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo()
    }

    @Test
    void createRandomMatchComplexCondition() throws Exception{
        //given
        createTestDataUtils.signUpUsers();
        createTestDataUtils.createMatchCondition();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        RandomMatchDto randomMatchDtoSorkim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
                .build())
            .build();

        RandomMatchDto randomMatchDtoTakim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.EATOUT))
                .build())
            .build();
        RandomMatchDto randomMatchDtoHyenam = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.EATOUT))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim1 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                .wayOfEatingList(List.of(WayOfEating.TAKEOUT))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim2 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of( Place.SEOCHO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim3 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        randomMatchService.createRandomMatch("takim1@student.42Seoul.kr", randomMatchDtoTakim1);
        randomMatchService.createRandomMatch("takim2@student.42Seoul.kr", randomMatchDtoTakim2);
        randomMatchService.createRandomMatch("takim3@student.42Seoul.kr", randomMatchDtoTakim3);
        randomMatchService.createRandomMatch("sorkim@student.42Seoul.kr", randomMatchDtoSorkim);
        randomMatchService.createRandomMatch("takim@student.42Seoul.kr", randomMatchDtoTakim);
        randomMatchService.createRandomMatch("hyenam@student.42Seoul.kr", randomMatchDtoHyenam);

    }

    @Test
    void createRandomMatchStudyComplexCondition() throws Exception{
        //given
        createTestDataUtils.signUpUsers();
        createTestDataUtils.createMatchCondition();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        RandomMatchDto randomMatchDtoSorkim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE, TypeOfStudy.NOT_INNER_CIRCLE))
                .build())
            .build();

        RandomMatchDto randomMatchDtoTakim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE, TypeOfStudy.NOT_INNER_CIRCLE))
                .build())
            .build();
        RandomMatchDto randomMatchDtoHyenam = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .typeOfStudyList(List.of(TypeOfStudy.NOT_INNER_CIRCLE))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim1 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                .typeOfStudyList(List.of(TypeOfStudy.NOT_INNER_CIRCLE))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim2 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of( Place.SEOCHO))
                .typeOfStudyList(List.of(TypeOfStudy.NOT_INNER_CIRCLE))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim3 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .build())
            .build();

        randomMatchService.createRandomMatch("takim1@student.42Seoul.kr", randomMatchDtoTakim1);
        randomMatchService.createRandomMatch("takim2@student.42Seoul.kr", randomMatchDtoTakim2);
        randomMatchService.createRandomMatch("takim3@student.42Seoul.kr", randomMatchDtoTakim3);
        randomMatchService.createRandomMatch("sorkim@student.42Seoul.kr", randomMatchDtoSorkim);
        randomMatchService.createRandomMatch("takim@student.42Seoul.kr", randomMatchDtoTakim);
        randomMatchService.createRandomMatch("hyenam@student.42Seoul.kr", randomMatchDtoHyenam);

    }

    @Test
    void createRandomMatchCreatedAt() throws Exception{
        //given
        createTestDataUtils.signUpUsers();
        createTestDataUtils.createMatchCondition();
        LocalDate tomorrow = LocalDate.now().plusDays(1);


        RandomMatchDto randomMatchDtoSorkim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();

        RandomMatchDto randomMatchDtoTakim = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        RandomMatchDto randomMatchDtoHyenam = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim1 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim2 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();
        RandomMatchDto randomMatchDtoTakim3 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of(Place.GAEPO))
                .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                .build())
            .build();


        randomMatchService.createRandomMatch("sorkim@student.42Seoul.kr", randomMatchDtoSorkim);
        randomMatchService.createRandomMatch("takim@student.42Seoul.kr", randomMatchDtoTakim);
        randomMatchService.createRandomMatch("hyenam@student.42Seoul.kr", randomMatchDtoHyenam);
        randomMatchService.createRandomMatch("takim1@student.42Seoul.kr", randomMatchDtoTakim1);
        randomMatchService.createRandomMatch("takim2@student.42Seoul.kr", randomMatchDtoTakim2);
        randomMatchService.createRandomMatch("takim3@student.42Seoul.kr", randomMatchDtoTakim3);

    }
}