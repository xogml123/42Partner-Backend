package partner42.modulecommon.producer.random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import partner42.modulecommon.producer.MatchMakingEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class RandomMatchProducer {

    private final KafkaTemplate<String, MatchMakingEvent> kafkaTemplate;

    @Value("${kafka.topic.match-making.name}")
    private String topicName;

    /**
     * 여러 consumer에서 동시에 consume될 경우 Exception이 발생할 수 있기 때문에 key 값 설정.
     * @param matchMakingEvent
     */
    public void send(MatchMakingEvent matchMakingEvent) {
        kafkaTemplate.send(topicName, topicName, matchMakingEvent);
        log.debug("matchMaking kafka produce");
    }
}
