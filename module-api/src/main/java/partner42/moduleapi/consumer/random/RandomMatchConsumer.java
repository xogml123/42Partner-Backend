package partner42.moduleapi.consumer.random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import partner42.moduleapi.service.random.MatchMakingService;
import partner42.modulecommon.producer.MatchMakingEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomMatchConsumer {
    private final MatchMakingService matchMakingService;

    /**
     * @param matchMakingEvent
     * @param ack
     */
    @KafkaListener(topics = "${kafka.topic.match-making.name}", groupId = "${kafka.consumer.match-making.rdb-group-id}",
        containerFactory = "kafkaListenerContainerFactoryMatchMakingEvent")
    public void matchMakingConsumerGroup(@Payload MatchMakingEvent matchMakingEvent, Acknowledgment ack) {
        log.info("matchMakingConsumerGroup");
        matchMakingService.matchMaking(matchMakingEvent.getNow());
        ack.acknowledge();
    }
}
