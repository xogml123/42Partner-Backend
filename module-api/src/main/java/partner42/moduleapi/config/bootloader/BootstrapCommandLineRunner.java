package partner42.moduleapi.config.bootloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.domain.model.sse.SseEventName;

//테스트 케이스 입력용
@RequiredArgsConstructor
@Component
@Slf4j
public class BootstrapCommandLineRunner implements CommandLineRunner {

    private final BootstrapDataLoader bootstrapDataLoader;
    private final RedisMessageListenerContainer container;
    private final AlarmService alarmService;

    @Value("${spring.jpa.hibernate.data-loader}")
    private String dataLoader;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (dataLoader.equals("1")) {
            bootstrapDataLoader.createDefaultUsers();
            bootstrapDataLoader.createMatchCondition();
        }
    }
}
