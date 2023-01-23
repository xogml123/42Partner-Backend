package partner42.modulecommon.repository.sse;


import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSERepository {

    public void put(String key, SseEmitter sseEmitter);

    public SseEmitter get(String key);

    public List<SseEmitter> getByPrefix(String prefix) ;
}
