package partner42.moduleapi.service.alarm;


import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import partner42.moduleapi.dto.alarm.AlarmArgsDto;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.SseException;
import partner42.modulecommon.repository.alarm.AlarmRepository;
import partner42.modulecommon.domain.model.sse.SseRepositoryKeyRule;
import partner42.modulecommon.repository.sse.SSERepository;
import partner42.modulecommon.repository.user.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService {
    @Value("${sse.timeout}")
    private String sseTimeout;
    private static final String UNDER_SCORE = "_";
    private static final String CONNECTED = "CONNECTED";
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final SSERepository sseRepository;
    private final RedisTemplate<String, String> redisTemplate;



    @Transactional
    public Slice<AlarmDto> sendAlarmSliceAndIsReadToTrue(Pageable pageable, String username) {
        Member member = getUserByUsernameOrException(username).getMember();
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByMemberId(pageable, member.getId());
        List<Alarm> alarms = alarmSlices.getContent();

        //update 쿼리 여러번 나가는지 확인 해봐야함.

        SliceImpl<AlarmDto> alarmDtos = new SliceImpl<>(alarms.stream()
            .map(alarm ->
                AlarmDto.builder()
                    .alarmId(alarm.getApiId())
                    .text(alarm.getAlarmType().getAlarmContent())
                    .alarmArgsDto(AlarmArgsDto.builder()
                        .articleId(alarm.getAlarmArgs().getArticleId())
                        .opinionId(alarm.getAlarmArgs().getOpinionId())
                        .callingMemberNickname(alarm.getAlarmArgs().getCallingMemberNickname())
                        .build())
                    .isRead(alarm.getIsRead())
                    .build())
            .collect(Collectors.toList()),
            alarmSlices.getPageable(), alarmSlices.hasNext());
        alarmRepository.bulkUpdateAlarmIsReadToTrueInIdList(alarms.stream()
            .map(Alarm::getId)
            .collect(Collectors.toList()));
        return alarmDtos;
    }

    public void send(Long alarmReceiverId,SseEventName sseEventName) {
        redisTemplate.convertAndSend(sseEventName.getValue(),
            getRedisPubMessage(alarmReceiverId, sseEventName));
    }
    @Transactional
    public void createAlarm(Long alarmReceiverId, AlarmType alarmType, AlarmArgs alarmArgs) {
        User alarmReceiver = userRepository.findById(alarmReceiverId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        Alarm alarm = Alarm.of(alarmType, alarmArgs, alarmReceiver.getMember());
        alarmRepository.save(alarm);
    }

    public SseEmitter subscribe(String username, String lastEventId, LocalDateTime now) {
        Long userId = getUserByUsernameOrException(username).getId();
        SseEmitter sse = new SseEmitter(Long.parseLong(sseTimeout));
        String key = new SseRepositoryKeyRule(userId, SseEventName.ALARM_LIST,
            now).toCompleteKeyWhichSpecifyOnlyOneValue();

        sse.onCompletion(() -> {
            log.info("onCompletion callback");
            sseRepository.remove(key);
        });
        sse.onTimeout(() -> {
            log.info("onTimeout callback");
            //만료 시 Repository에서 삭제 되어야함.
            sse.complete();
        });

        sseRepository.put(key, sse);
        try {
            sse.send(SseEmitter.event()
                .name(CONNECTED)
                .id(getEventId(userId, now, SseEventName.ALARM_LIST))
                .data("subscribe"));
        } catch (IOException exception) {
            sseRepository.remove(key);
            log.info("SSE Exception: {}", exception.getMessage());
            throw new SseException(ErrorCode.SSE_SEND_ERROR);
        }

        // 중간에 연결이 끊겨서 다시 연결할 때, lastEventId를 통해 기존의 받지못한 이벤트를 받을 수 있도록 할 수 있음.
        // 한번의 알림이나 새로고침을 받으면 알림을 slice로 가져오기 때문에
        // 수신 못한 응답을 다시 보내는 로직을 구현하지 않음.
        return sse;
    }

    /**
     *  특정 유저의 특정 sse 이벤트에 대한 id를 생성한다.
     *  위 조건으로 여러개 정의 될 경우 now 로 구분한다.
     * @param userId
     * @param now
     * @param eventName
     * @return
     */
    private String getEventId(Long userId, LocalDateTime now, SseEventName eventName) {
        return userId + UNDER_SCORE + eventName.getValue() + UNDER_SCORE + now;
    }

    /**
     * redis pub시 userId와 sseEventName을 합쳐서 보낸다.
     * @param userId
     * @param sseEventName
     * @return
     */
    private String getRedisPubMessage(Long userId, SseEventName sseEventName) {
        return userId + UNDER_SCORE + sseEventName.getValue();
    }
    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }

}
