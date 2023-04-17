package partner42.modulecommon.repository.sse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SSEInMemoryRepository implements SSERepository{
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void put(String key, SseEmitter sseEmitter) {
        sseEmitterMap.put(key, sseEmitter);
    }
    @Override
    public Optional<SseEmitter> get(String key) {
        return Optional.ofNullable(sseEmitterMap.get(key));
    }
    @Override
    public List<SseEmitter> getListByKeyPrefix(String keyPrefix){
        return sseEmitterMap.keySet().stream()
            .filter(key -> key.startsWith(keyPrefix))
            .map(sseEmitterMap::get)
            .collect(Collectors.toList());
    }
    @Override
    public List<String> getKeyListByKeyPrefix(String keyPrefix){
        return sseEmitterMap.keySet().stream()
            .filter(key -> key.startsWith(keyPrefix))
            .collect(Collectors.toList());
    }
    @Override
    public void remove(String key) {
        sseEmitterMap.remove(key);
    }
}
