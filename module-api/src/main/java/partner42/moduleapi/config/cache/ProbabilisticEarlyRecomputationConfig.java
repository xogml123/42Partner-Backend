package partner42.moduleapi.config.cache;

import java.util.List;
import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ProbabilisticEarlyRecomputationConfig {

    @Bean
    public Random random() {
        return new Random();
    }
}
