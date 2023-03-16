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
import partner42.modulecommon.repository.sse.SSEInMemoryRepository;
import partner42.modulecommon.domain.model.sse.SseRepositoryKeyRule;
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
    private final SSEInMemoryRepository sseRepository;


    private final RedisTemplate<String, String> redisTemplate;


    /**
     * 여러 서버에서 SSE를 구현하기 위한 Redis Pub/Sub CommandLineRunner에서 subscribe해두었던 topic에 publish가 일어나면 이
     * 메서드가 호출된다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] strings = message.toString().split(UNDER_SCORE);
        Long userId = Long.parseLong(strings[0]);
        SseEventName eventName = SseEventName.getEnumFromValue(strings[1]);
        String keyPrefix = new SseRepositoryKeyRule(userId, eventName,
            null).toKeyUserAndEventInfo();

        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        sseRepository.getKeyListByKeyPrefix(keyPrefix).forEach(key -> {
            SseEmitter emitter = sseRepository.get(key).get();
            try {
                emitter.send(SseEmitter.event()
                    .id(getEventId(userId, now, eventName))
                    .name(eventName.getValue())
                    .data(eventName.getValue()));
            } catch (IOException e) {
                sseRepository.remove(key);
                log.error("SSE send error", e);
                throw new SseException(ErrorCode.SSE_SEND_ERROR);
            }
        });
    }

    @Transactional
    public Slice<AlarmDto> sendAlarmSliceAndIsReadToTrue(Pageable pageable, String username) {
        Member member = getUserByUsernameOrException(username).getMember();
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByMemberId(pageable, member.getId());
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

    public void send(Long userId, AlarmType alarmType, AlarmArgs alarmArgs, SseEventName sseEventName) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        Alarm alarm = Alarm.of(alarmType, alarmArgs, user.getMember());
        alarmRepository.save(alarm);
        redisTemplate.convertAndSend(sseEventName.getValue(),
            userId + UNDER_SCORE + sseEventName.getValue());

    }

    public SseEmitter subscribe(String username, String lastEventId) {
        Long userId = getUserByUsernameOrException(username).getId();
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        SseEmitter sse = new SseEmitter(Long.parseLong(sseTimeout));

        String key = new SseRepositoryKeyRule(userId, SseEventName.ALARM_LIST,
            now).toCompleteKeyWhichSpecifyOnlyOneValue();

        sse.onCompletion(() -> {
            log.info("onCompletion callback");
            //만료 시 Repository에서 삭제 되어야함.

            sseRepository.remove(key);
        });
        sse.onTimeout(() -> {
            log.info("onTimeout callback");
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
        // 현재 로직상에서는 어처피 한번의 알림이나 새로고침을 받으면 알림 list를 paging해서 가져오기 때문에
        // 수신 못한 응답을 다시 보내는게 불 필요하고 효율을 떨어뜨릴 수 있음.
        return sse;
    }

    private String getEventId(Long userId, LocalDateTime now, SseEventName eventName) {
        return userId + UNDER_SCORE + eventName.getValue() + UNDER_SCORE + now;
    }


    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }


}
