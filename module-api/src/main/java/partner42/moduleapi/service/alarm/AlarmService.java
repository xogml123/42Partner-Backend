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
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import partner42.moduleapi.dto.alarm.AlarmArgsDto;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.SseException;
import partner42.modulecommon.repository.alarm.AlarmRepository;
import partner42.modulecommon.repository.sse.SSERepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CustomTimeUtils;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService implements MessageListener {
    @Value("${sse.timeout}")
    private String sseTimeout;
    private static final String UNDER_SCORE = "_";
    private static final String CONNECTED = "CONNECTED";
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final SSERepository sseRepository;

    private final RedisMessageListenerContainer container;

    @Transactional
    public Slice<AlarmDto> sendAlarmSliceAndIsReadToTrue(Pageable pageable, String username) {
        Member member = getUserByUsernameOrException(username).getMember();
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByCondition(pageable, member.getId());
        List<Alarm> alarms = alarmSlices.getContent();

        //update 쿼리 여러번 나가는지 확인 해봐야함.
        alarms.forEach(Alarm::read);
        return new SliceImpl<>(alarms.stream()
            .map(alarm ->
                AlarmDto.builder()
                    .alarmId(alarm.getApiId())
                    .text(alarm.getAlarmType().getAlarmContent())
                    .alarmArgsDto(AlarmArgsDto.builder()
                        .articleId(alarm.getAlarmArgs().getArticleId())
                        .opinionId(alarm.getAlarmArgs().getOpinionId())
                        .callingMemberId(alarm.getAlarmArgs().getCallingMemberNickname())
                        .build())
                    .build())
            .collect(Collectors.toList()),
            alarmSlices.getPageable(), alarmSlices.hasNext());
    }

    public void send(AlarmType type, AlarmArgs args, Member member, SseEventName eventName) {
        Alarm alarm = Alarm.of(type, args, member);
        alarmRepository.save(alarm);
        User user = member.getUser();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        sseRepository.getList(user, eventName.getName()).forEach(it -> {
                try {
                    it.send(SseEmitter.event()
                        .id(getEventId(user, now, eventName))
                        .name(eventName.getName())
                        .data(eventName.getName()));
                } catch (IOException exception) {
                    sseRepository.remove(user, now, eventName.getName());
                    log.info("SSE Exception: {}", exception.getMessage());
                    throw new SseException(ErrorCode.SSE_SEND_ERROR);
                }
            }
        );
    }

    public SseEmitter subscribe(String username, String lastEventId) {
        User user = getUserByUsernameOrException(username);
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        SseEmitter sse = new SseEmitter(Long.parseLong(sseTimeout));
        sse.onCompletion(() -> {
            log.info("onCompletion callback");
            //만료 시 Repository에서 삭제 되어야함.

            sseRepository.remove(user, now, SseEventName.ALARM_LIST.getName());
        });
        sse.onTimeout(() -> {
            log.info("onTimeout callback");
            sse.complete();
        });
        sseRepository.put(user, now, SseEventName.ALARM_LIST.getName(), sse);
        try {
            sse.send(SseEmitter.event()
                .name(CONNECTED)
                .id(getEventId(user, now, SseEventName.ALARM_LIST))
                .data("subscribe"));
        } catch (IOException exception) {
            sseRepository.remove(user, now, SseEventName.ALARM_LIST.getName());
            log.info("SSE Exception: {}", exception.getMessage());
            throw new SseException(ErrorCode.SSE_SEND_ERROR);
        }

        // 중간에 연결이 끊겨서 다시 연결할 때, lastEventId를 통해 기존의 받지못한 이벤트를 받을 수 있도록 함.
        // 현재 로직상에서는 어처피 한번의 알림이나 새로고침을 받으면 알림 list를 paging해서 가져오기 때문에 불 필요하고 효율을 떨어뜨릴 수 있음.
//        if (lastEventId != null) {
//            LocalDateTime lastEventTime = LocalDateTime.parse(lastEventId.split("_")[2]);
//            sseRepository.getKeyList(user, SseEventName.ALARM_LIST.getName()LARM_LIST.getName()).stream()
//                .filter(key ->
//                    LocalDateTime.parse(key.split("_")[2]).isAfter(lastEventTime)
//                )
//                .forEach(key -> {
//                    SseEmitter sseEmitter = sseRepository.get(user, now, SseEventName.ALARM_LIST.getName()LARM_LIST.getName());
//                    try {
//                        sseEmitter.send(SseEmitter.event()
//                            .name(SseEventName.ALARM_LIST.getName()LARM_LIST.getName())
//                            .id(key)
//                            .data("alarmList"));
//                    } catch (IOException e) {
//                        throw new SseException(ErrorCode.SSE_SEND_ERROR);
//                    }
//                });
//        }
        return sse;
    }

    private String getEventId(User user, LocalDateTime now, SseEventName eventName) {
        return user.getId() + UNDER_SCORE + eventName.getName() + UNDER_SCORE + now;
    }


    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }
}
