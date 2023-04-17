package partner42.moduleapi.service.random;


import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.config.ServiceWithDAOTestDefaultConfig;
import partner42.moduleapi.config.kafka.AlarmEvent;
import partner42.moduleapi.dto.match.MatchMakingDto;
import partner42.moduleapi.dto.random.MealRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDtoFactory;
import partner42.moduleapi.dto.random.StudyRandomMatchDto;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.match.ConditionCategory;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MatchMakingService.class, RandomMatchService.class, RandomMatchDtoFactory.class,
    ServiceWithDAOTestDefaultConfig.class,
})
class MatchMakingServiceWithDAOTest {

    @Autowired
    private MatchMakingService matchMakingService;
    @Autowired
    private RandomMatchService randomMatchService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchConditionMatchRepository matchConditionMatchRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
//    @Transactional(propagation = Propagation.NEVER)
    void matchMaking_whenMatchNotExpected_thenReturnDtoFieldAllEmptyList() {
        //given
        LocalDateTime now = LocalDateTime.now();
        MealRandomMatchDto mealDto1 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.GAEPO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        MealRandomMatchDto mealDto2 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.SEOCHO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        randomMatchService.createRandomMatch("takim", mealDto1, now);
        randomMatchService.createRandomMatch("sorkim", mealDto2, now);
        //when
        MatchMakingDto matchMakingDto = matchMakingService.matchMaking(now);
        List<Match> matches = matchRepository.findAll();

        //then
        assertThat(matches).isEmpty();
        assertThat(matchMakingDto).extracting(MatchMakingDto::getEmails,
                MatchMakingDto::getAlarmEvents)
            .containsExactly(List.of(), List.of());
    }

    @Test
//    @Transactional(propagation = Propagation.NEVER)
    void matchMaking_whenMatchExpected_thenAssertDtoField() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        LocalDateTime now = LocalDateTime.now();
        MealRandomMatchDto mealDto1 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.GAEPO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        MealRandomMatchDto mealDto2 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.GAEPO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        randomMatchService.createRandomMatch("takim", mealDto1, now);
        randomMatchService.createRandomMatch("sorkim", mealDto2, now);
        //when
        MatchMakingDto matchMakingDto = matchMakingService.matchMaking(now);
        List<Match> matches = matchRepository.findAll();
        //then
        assertThat(matches).hasSize(1);
        assertThat(matchMakingDto.getEmails())
            .flatExtracting(emails -> emails.stream()
                .collect(Collectors.toList()))
            .containsExactlyInAnyOrder(takim.getEmail(), sorkim.getEmail());
        assertThat(matchMakingDto.getAlarmEvents())
            .flatExtracting(alarmEvents -> alarmEvents.stream()
                .collect(Collectors.toList()))
            .extracting(AlarmEvent::getUserId, AlarmEvent::getType, AlarmEvent::getEventName,
                AlarmEvent::getArgs)
            .containsExactlyInAnyOrder(
                tuple(takim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()),
                tuple(sorkim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()
                ));
    }

    /**
     * 실제 동작시에는 전체 범위에서 부모 트랜잭션이 걸리지 않고 동작하고
     * 이를 지키지 않으면 영속화 되지 않은 엔티티의 상태변경이 바로 DB에 반영되는 문제가 있어 update쿼리가 추가로 나가지만
     * 테스트 결과에는 문제가 없기 때문에 해당 테스트에서 테스트를 위해 트랜잭션을 걸었음.
     */
    @Test
//    @Transactional(propagation = Propagation.NEVER)
    void matchMaking_whenBothContentCategoryMatchExpected_thenTwoMatchCreated() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDateTime now = LocalDateTime.now();
        MealRandomMatchDto mealDto1 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.GAEPO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        MealRandomMatchDto mealDto2 = MealRandomMatchDto.builder()
            .placeList(List.of(Place.GAEPO))
            .wayOfEatingList(List.of(WayOfEating.DELIVERY, WayOfEating.TAKEOUT))
            .contentCategory(ContentCategory.MEAL)
            .build();
        StudyRandomMatchDto studyDto1 = StudyRandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .placeList(List.of(Place.SEOCHO))
            .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE, TypeOfStudy.NOT_INNER_CIRCLE))
            .build();
        StudyRandomMatchDto studyDto2 = StudyRandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .placeList(List.of(Place.SEOCHO, Place.GAEPO))
            .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
            .build();
        randomMatchService.createRandomMatch("takim", mealDto1, now);
        randomMatchService.createRandomMatch("sorkim", mealDto2, now);
        randomMatchService.createRandomMatch("takim", studyDto1, now);
        randomMatchService.createRandomMatch("sorkim", studyDto2, now);
        //when
        MatchMakingDto matchMakingDto = matchMakingService.matchMaking(now);
        List<Match> matches = matchRepository.findAll();
        matchConditionMatchRepository.findAll();
        //then
        assertThat(matches)
            .hasSize(2)
            .extracting(Match::getContentCategory, Match::getMatchStatus, Match::getParticipantNum)
            .containsExactlyInAnyOrder(
                tuple(ContentCategory.MEAL, MatchStatus.MATCHED, 2),
                tuple(ContentCategory.STUDY, MatchStatus.MATCHED, 2)
            );

        assertThat(matches).extracting(Match::getMatchConditionMatches)
            .flatExtracting(mcms -> mcms.stream()
                .map(MatchConditionMatch::getMatchCondition)
                .collect(Collectors.toList()))
            .extracting(MatchCondition::getConditionCategory, MatchCondition::getValue)
            .containsExactlyInAnyOrder(
                tuple(ConditionCategory.Place, Place.GAEPO.name()),
                tuple(ConditionCategory.WayOfEating, WayOfEating.DELIVERY.name()),
                tuple(ConditionCategory.Place, Place.SEOCHO.name()),
                tuple(ConditionCategory.TypeOfStudy, TypeOfStudy.INNER_CIRCLE.name())
            );

        assertThat(matchMakingDto.getEmails())
            .flatExtracting(emails -> emails.stream()
                .collect(Collectors.toList()))
            .containsExactlyInAnyOrder(takim.getEmail(), sorkim.getEmail(), takim.getEmail(),
                sorkim.getEmail());
        assertThat(matchMakingDto.getAlarmEvents())
            .flatExtracting(alarmEvents -> alarmEvents.stream()
                .collect(Collectors.toList()))
            .extracting(AlarmEvent::getUserId, AlarmEvent::getType, AlarmEvent::getEventName,
                AlarmEvent::getArgs)
            .containsExactlyInAnyOrder(
                tuple(takim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()),
                tuple(sorkim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()),
                tuple(takim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()),
                tuple(sorkim.getId(), AlarmType.MATCH_CONFIRMED, SseEventName.ALARM_LIST,
                    AlarmArgs.builder()
                        .callingMemberNickname("system")
                        .build()
                ));
    }
}