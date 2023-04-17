package partner42.modulecommon.repository.sse;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SSEInMemoryRepositoryTest {

    @BeforeEach
    void setUp() {
    }



    @Test
    void get() {
        //given
        SSEInMemoryRepository sseInMemoryRepository = new SSEInMemoryRepository();
        sseInMemoryRepository.put("key", new SseEmitter());
        //when
        Optional<SseEmitter> optionalSseEmitterNotEmpty = sseInMemoryRepository.get("key");
        Optional<SseEmitter> optionalSseEmitterEmpty = sseInMemoryRepository.get("NotExistKey");
        //then
        assertThat(optionalSseEmitterNotEmpty).isPresent();
        assertThat(optionalSseEmitterEmpty).isNotPresent();

    }

    @Test
    void getListByKeyPrefix() {
        //given
        SSEInMemoryRepository sseInMemoryRepository = new SSEInMemoryRepository();
        SseEmitter sseEmitter1 = new SseEmitter();
        SseEmitter sseEmitter2 = new SseEmitter();
        SseEmitter sseEmitter3 = new SseEmitter();
        SseEmitter sseEmitter4 = new SseEmitter();

        sseInMemoryRepository.put("key", sseEmitter1);
        sseInMemoryRepository.put("keyPrefix", sseEmitter2);
        sseInMemoryRepository.put("frontKey", sseEmitter3);
        sseInMemoryRepository.put("ke12", sseEmitter4);
        //when
        List<SseEmitter> sseEmitters = sseInMemoryRepository.getListByKeyPrefix("key");
        //then
        assertThat(sseEmitters).containsOnly(sseEmitter1, sseEmitter2);
    }

    @Test
    void getKeyListByKeyPrefix() {

        SSEInMemoryRepository sseInMemoryRepository = new SSEInMemoryRepository();
        SseEmitter sseEmitter1 = new SseEmitter();
        SseEmitter sseEmitter2 = new SseEmitter();
        SseEmitter sseEmitter3 = new SseEmitter();
        SseEmitter sseEmitter4 = new SseEmitter();

        sseInMemoryRepository.put("key", sseEmitter1);
        sseInMemoryRepository.put("keyPrefix", sseEmitter2);
        sseInMemoryRepository.put("frontKey", sseEmitter3);
        sseInMemoryRepository.put("ke12", sseEmitter4);
        //when
        List<String> sseEmitters = sseInMemoryRepository.getKeyListByKeyPrefix("key");
        //then
        assertThat(sseEmitters).containsOnly("key", "keyPrefix");
    }

    @Test
    void remove() {
    }
}