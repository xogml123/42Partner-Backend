package partner42.moduleapi.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages =  "partner42.modulecommon.repository")
@EntityScan(basePackages = "partner42.modulecommon.domain")
@Configuration
public class JPAConfig {

}
