package partner42.moduleapi.config.JpaPackage;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages =  "partner42.modulecommon.repository")
@EntityScan(basePackages = "partner42.modulecommon.domain")
@Configuration
public class JpaAndEntityPackagePathConfig {

}
