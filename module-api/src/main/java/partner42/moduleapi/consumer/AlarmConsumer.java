package partner42.moduleapi.consumer;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.kafka.AlarmEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmService alarmService;

    /**
     * offset을 최신으로 설정해야한다.
     * https://stackoverflow.com/questions/57163953/kafkalistener-consumerconfig-auto-offset-reset-doc-earliest-for-multiple-listene
     * @param alarmEvent
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "${kafka.consumer.alarm.group.id}",
        properties = {AUTO_OFFSET_RESET_CONFIG + ":earliest"})
    public void consumeNotification(AlarmEvent alarmEvent, Acknowledgment ack) {
        log.info("Consume the event {}", alarmEvent);
        alarmService.send(alarmEvent.getUserId(), alarmEvent.getType(), alarmEvent.getArgs(),
            alarmEvent.getEventName());
        ack.acknowledge();
    }
}
