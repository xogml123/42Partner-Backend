package partner42.moduleapi.config.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import partner42.moduleapi.aop.OptimisticLockAspect;

/**
 * transactional annotation의 실행 순서를 지정.
 */
@EnableTransactionManagement(order = 100)
@Configuration
public class AopConfig {

    @Bean
    public OptimisticLockAspect optimisticLockAspect() {
        return new OptimisticLockAspect();
    }
}
