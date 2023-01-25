package partner42.modulecommon.repository.sse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SSEInMemoryRepository implements SSERepository {
    private static final String UNDER_SCORE = "_";
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void put(Object identifier, LocalDateTime now, String eventName, SseEmitter sseEmitter) {
        String key = makeKey(identifier, now, eventName);
        sseEmitterMap.put(key, sseEmitter);

    }
    @Override
    public SseEmitter get(Object identifier, LocalDateTime now, String eventName) {
        String key = makeKey(identifier, now, eventName);
        return sseEmitterMap.get(key);
    }
    @Override
    public List<SseEmitter> getList(Object identifier, String eventName) {
        String prefix = makeKeyPrefix(identifier, eventName);
        return sseEmitterMap.keySet().stream()
            .filter(key -> key.startsWith(prefix))
            .map(sseEmitterMap::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getKeyList(Object identifier, String eventName) {
        String prefix = makeKeyPrefix(identifier, eventName);
        return sseEmitterMap.keySet().stream()
            .filter(key -> key.startsWith(prefix))
            .collect(Collectors.toList());
    }

    @Override
    public void remove(Object identifier, LocalDateTime now, String eventName) {
        String key = makeKey(identifier, now, eventName);
        sseEmitterMap.remove(key);
    }

    private String makeKey(Object identifier, LocalDateTime now, String eventName) {
        return makeKeyPrefix(identifier, eventName) + UNDER_SCORE + now;
    }
    private String makeKeyPrefix(Object identifier, String eventName){
        return identifier + UNDER_SCORE + eventName;
    }
}
