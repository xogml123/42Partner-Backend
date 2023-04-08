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

    public void send(MatchMakingEvent matchMakingEvent) {
        kafkaTemplate.send(topicName, matchMakingEvent);
        log.debug("matchMaking kafka produce");
    }
}
