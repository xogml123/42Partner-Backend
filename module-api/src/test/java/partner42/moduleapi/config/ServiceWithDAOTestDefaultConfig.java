package partner42.moduleapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;

@TestConfiguration
@Configuration
@Import({Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class, BCryptPasswordEncoder.class})
public class ServiceWithDAOTestDefaultConfig {

}
