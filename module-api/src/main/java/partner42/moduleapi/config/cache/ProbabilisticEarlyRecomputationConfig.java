package partner42.moduleapi.config.cache;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProbabilisticEarlyRecomputationConfig {

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public RandomDoubleGenerator randomDoubleGenerator(Random random) {
        return new RandomDoubleGenerator(random);
    }

    /**
     * 테스트 할때 Random한 부분을 대체할 수 있게 하기 위해 사용.
     */
    @RequiredArgsConstructor
    public static class RandomDoubleGenerator  {
        private final Random random;
        public double nextDouble() {
            return random.nextDouble();
        }
    }

}
