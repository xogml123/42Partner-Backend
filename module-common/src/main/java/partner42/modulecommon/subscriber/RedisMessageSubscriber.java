package partner42.modulecommon.subscriber;

import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.sse.SseRepositoryKeyRule;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.SseException;
import partner42.modulecommon.repository.sse.SSEInMemoryRepository;
import partner42.modulecommon.utils.CustomTimeUtils;
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisMessageSubscriber implements MessageListener {
    private static final String UNDER_SCORE = "_";
    private final SSEInMemoryRepository sseRepository;
    /**
     * 여러 서버에서 SSE를 구현하기 위한 Redis Pub/Sub
     * subscribe해두었던 topic에 publish가 일어나면 메서드가 호출된다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Redis Pub/Sub message received: {}", message.toString());
        String[] strings = message.toString().split(UNDER_SCORE);
        Long userId = Long.parseLong(strings[0]);
        SseEventName eventName = SseEventName.getEnumFromValue(strings[1]);
        String keyPrefix = new SseRepositoryKeyRule(userId, eventName,
            null).toCompleteKeyWhichSpecifyOnlyOneValue();

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
}
