package partner42.modulecommon.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages =  "partner42.modulecommon.repository")
@EntityScan(basePackages = "partner42.modulecommon.domain")
@Configuration
public class JPAScanConfig {

}
