package partner42.modulecommon.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value("${kafka.topic.alarm.name}")
    private String topicName;
    @Value("${kafka.topic.alarm.numPartitions}")
    private String numPartitions;
    @Value("${kafka.topic.alarm.replicationFactor}")
    private String replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * broker 를 두개만 설정하였으므로 최소 Replication Factor로 2를 설정하고
     * Partition의 경우 Event 의 Consumer인 WAS를 2대까지만 실행되도록 해두었기 때문에 2로 설정함.
     * 이보다 Partition을 크게 설정한다고 해서 Consume 속도가 빨라지지 않기 때문이다.
     * @return
     */
    @Bean
    public NewTopic newTopic() {
        return new NewTopic(topicName, Integer.parseInt(numPartitions), Short.parseShort(replicationFactor));
    }
}
