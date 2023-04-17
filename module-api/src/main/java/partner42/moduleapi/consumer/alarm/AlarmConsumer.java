package partner42.moduleapi.consumer.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.moduleapi.config.kafka.AlarmEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmService alarmService;

    /**
     * offset을 최신으로 설정.
     * https://stackoverflow.com/questions/57163953/kafkalistener-consumerconfig-auto-offset-reset-doc-earliest-for-multiple-listene
     * @param alarmEvent
     * @param ack
     */
    @KafkaListener(topics = "${kafka.topic.alarm.name}", groupId = "${kafka.consumer.alarm.rdb-group-id}",
         containerFactory = "kafkaListenerContainerFactoryRDB")
    public void createAlarmInRDBConsumerGroup(@Payload AlarmEvent alarmEvent, Acknowledgment ack) {
        log.info("createAlarmInRDBConsumerGroup");
        alarmService.createAlarm(alarmEvent.getUserId(), alarmEvent.getType(), alarmEvent.getArgs());
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topic.alarm.name}", groupId = "${kafka.consumer.alarm.redis-group-id}",
         containerFactory = "kafkaListenerContainerFactoryRedis")
    public void redisPublishConsumerGroup(@Payload AlarmEvent alarmEvent, Acknowledgment ack) {
        log.info("redisPublishConsumerGroup");
        alarmService.send(alarmEvent.getUserId(),
            alarmEvent.getEventName());
        ack.acknowledge();
    }
}
