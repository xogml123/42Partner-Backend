package partner42.moduleapi.consumer.random;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import partner42.moduleapi.config.kafka.AlarmEvent;
import partner42.moduleapi.dto.match.MatchMakingDto;
import partner42.moduleapi.producer.alarm.AlarmProducer;
import partner42.moduleapi.service.random.MatchMakingService;
import partner42.moduleapi.producer.random.MatchMakingEvent;
import partner42.modulecommon.utils.slack.SlackBotService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomMatchConsumer {
    private final MatchMakingService matchMakingService;
    private final SlackBotService slackBotService;
    private final AlarmProducer alarmProducer;

    /**
     * @param matchMakingEvent
     * @param ack
     */
    @KafkaListener(topics = "${kafka.topic.match-making.name}", groupId = "${kafka.consumer.match-making.rdb-group-id}",
        containerFactory = "kafkaListenerContainerFactoryMatchMakingEvent")
    public void matchMakingConsumerGroup(@Payload MatchMakingEvent matchMakingEvent, Acknowledgment ack) {
        MatchMakingDto matchMakingDto = matchMakingService.matchMaking(matchMakingEvent.getNow());
        // 알림 생성.
        for (List<AlarmEvent> alarmEvents: matchMakingDto.getAlarmEvents()){
            alarmEvents.forEach(alarmProducer::send);
        }
        //slack에 장애가 발생하여
        //알림이 보내지지 않아도 ack를 보내야 함.
        matchMakingDto.getEmails().forEach( emails -> {
            try{
                slackBotService.createSlackMIIM(emails);
            } catch(Exception e){
                log.error("slackBotService.createSlackMIIM(matchMakingDto) error");
            }
        });
        ack.acknowledge();
    }
}
