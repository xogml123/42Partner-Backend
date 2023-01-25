package partner42.modulecommon.repository.sse;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSERepository {

    public void put(Object identifier, LocalDateTime now, String eventName, SseEmitter sseEmitter);
    public Optional<SseEmitter> get(Object identifier, LocalDateTime now, String eventName);
    public List<SseEmitter> getList(Object identifier, String eventName);

    public void remove(Object identifier, LocalDateTime now, String eventName);
}
