package partner42.moduleapi.config.cache;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProbabilisticEarlyRecomputationConfig {

    @Bean
    public Random random() {
        return new Random();
    }

}
