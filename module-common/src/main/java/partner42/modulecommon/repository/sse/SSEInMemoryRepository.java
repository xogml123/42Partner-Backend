package partner42.modulecommon.repository.sse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SSEInMemoryRepository implements SSERepository {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public void put(String key, SseEmitter sseEmitter) {
        sseEmitterMap.put(key, sseEmitter);
    }

    public SseEmitter get(String key) {
        return sseEmitterMap.get(key);
    }

    public List<SseEmitter> getByPrefix(String prefix) {
        return sseEmitterMap.keySet().stream()
            .filter(key -> key.startsWith(prefix))
            .map(sseEmitterMap::get)
            .collect(Collectors.toList());
    }

}
