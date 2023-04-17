package partner42.modulecommon.repository.sse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSERepository {

    void put(String key, SseEmitter sseEmitter);

    Optional<SseEmitter> get(String key);

    List<SseEmitter> getListByKeyPrefix(String keyPrefix);

    List<String> getKeyListByKeyPrefix(String keyPrefix);

    void remove(String key);
}
