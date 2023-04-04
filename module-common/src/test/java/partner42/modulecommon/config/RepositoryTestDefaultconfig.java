package partner42.modulecommon.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;

@TestConfiguration
@Import({Auditor.class, QuerydslConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class})
public class RepositoryTestDefaultconfig {

}
