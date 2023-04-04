package partner42.modulecommon.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import partner42.modulecommon.config.kafka.AlarmEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlarmProducer {

    private final KafkaTemplate<String, AlarmEvent> kafkaTemplate;

    @Value("${kafka.topic.alarm.name}")
    private String topicName;

    public void send(AlarmEvent alarmEvent) {
        kafkaTemplate.executeInTransaction(producer ->
            producer.send(topicName, alarmEvent));

            log.debug("alarm kafka produce");
    }

}
