package partner42.moduleapi.service.alarm;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import partner42.moduleapi.annotation.ServiceWithDaoTest;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.dto.alarm.AlarmArgsDto;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.alarm.AlarmRepository;
import partner42.modulecommon.repository.sse.SSEInMemoryRepository;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AlarmService.class, SSEInMemoryRepository.class,
    Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class, BCryptPasswordEncoder.class})
class AlarmServiceWithDaoTest {

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
    }


    @Test
    void sendAlarmSliceAndIsReadToTrue_when_thenResponse() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        boolean isRead = false;

        //when
        Alarm alarm1 = alarmRepository.save(
            Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, AlarmArgs.builder()
                .callingMemberNickname("sorkim")
                .build(), takim.getMember()));
        Alarm alarm2 = alarmRepository.save(Alarm.of(AlarmType.PARTICIPATION_ON_MY_POST, AlarmArgs.builder()
            .callingMemberNickname("sorkim")
            .build(), takim.getMember()));
        Alarm alarm3 = alarmRepository.save(Alarm.of(AlarmType.MATCH_CONFIRMED, AlarmArgs.builder()
            .callingMemberNickname("sorkim")
            .build(), takim.getMember()));
        Slice<AlarmDto> alarmDtos = alarmService.sendAlarmSliceAndIsReadToTrue(
            PageRequest.of(0, 10), takim.getUsername());
        sorkim = userRepository.findByUsername("sorkim").get();
        //then
        assertThat(alarmDtos.getContent())
            .extracting(AlarmDto::getAlarmId, AlarmDto::getText, AlarmDto::getIsRead)
            .containsExactlyInAnyOrder(
                tuple(alarm1.getApiId(), alarm1.getAlarmType().getAlarmContent(), isRead),
                tuple(alarm2.getApiId(), alarm2.getAlarmType().getAlarmContent(), isRead),
                tuple(alarm3.getApiId(), alarm3.getAlarmType().getAlarmContent(), isRead)
            );

        assertThat(alarmDtos.getContent())
            .extracting(AlarmDto::getAlarmArgsDto)
            .extracting(AlarmArgsDto::getCallingMemberNickname, AlarmArgsDto::getArticleId,
                AlarmArgsDto::getOpinionId)
            .containsExactlyInAnyOrder(
                tuple(sorkim.getMember().getNickname(), null, null),
                tuple(sorkim.getMember().getNickname(), null, null),
                tuple(sorkim.getMember().getNickname(), null, null));
    }

    @Test
    void sendAlarmSliceAndIsReadToTrue_whenGetAlarmDtosTwice_thenIsReadTrue() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        boolean isRead = true;

        //when
        Alarm alarm1 = alarmRepository.save(
            Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, AlarmArgs.builder()
                .callingMemberNickname("takim")
                .build(), takim.getMember()));
        Alarm alarm2 = alarmRepository.save(Alarm.of(AlarmType.PARTICIPATION_ON_MY_POST, AlarmArgs.builder()
            .callingMemberNickname("takim")
            .build(), takim.getMember()));
        Alarm alarm3 = alarmRepository.save(Alarm.of(AlarmType.MATCH_CONFIRMED, AlarmArgs.builder()
            .callingMemberNickname("takim")
            .build(), takim.getMember()));
        //page size 다르게 두번 조회
        alarmService.sendAlarmSliceAndIsReadToTrue(
            PageRequest.of(0, 2, Sort.by(Order.asc("createdAt"))), takim.getUsername());
        Slice<AlarmDto> alarmDtosSecond = alarmService.sendAlarmSliceAndIsReadToTrue(
            PageRequest.of(0, 3, Sort.by(Order.asc("createdAt"))), takim.getUsername());
        //then
        assertThat(alarmDtosSecond.getContent())
            .extracting(AlarmDto::getAlarmId, AlarmDto::getText, AlarmDto::getIsRead)
            .containsExactlyInAnyOrder(
                tuple(alarm1.getApiId(), alarm1.getAlarmType().getAlarmContent(), isRead),
                tuple(alarm2.getApiId(), alarm2.getAlarmType().getAlarmContent(), isRead),
                //alarm3만 처음 읽혀지므로 isRead false
                tuple(alarm3.getApiId(), alarm3.getAlarmType().getAlarmContent(), false)
            );
    }

    @Test
    void send_whenCalled_thenAlarmEntitySaved() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        AlarmArgs alarmArgs = AlarmArgs.builder()
            .callingMemberNickname("sorkim")
            .build();
        AlarmType alarmType = AlarmType.COMMENT_ON_MY_COMMENT;

        //when
        alarmService.send(takim.getId(), alarmType, alarmArgs, SseEventName.ALARM_LIST);
        //then
        assertThat(alarmRepository.findAll())
            .extracting(Alarm::getAlarmType, Alarm::getAlarmArgs, Alarm::getCalledMember)
            .containsExactlyInAnyOrder(
                tuple(alarmType, alarmArgs, takim.getMember())
            );
    }

    @Test
    void send_verifyRedisTemplate_convertAndSend() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        AlarmArgs alarmArgs = AlarmArgs.builder()
            .callingMemberNickname("sorkim")
            .build();
        AlarmType alarmType = AlarmType.COMMENT_ON_MY_COMMENT;

        //when
        alarmService.send(takim.getId(), alarmType, alarmArgs, SseEventName.ALARM_LIST);
        //then
        verify(redisTemplate).convertAndSend(SseEventName.ALARM_LIST.getValue(),
            takim.getId() + "_" + SseEventName.ALARM_LIST.getValue());
    }

    @Test
    void subscribe() {
    }
}