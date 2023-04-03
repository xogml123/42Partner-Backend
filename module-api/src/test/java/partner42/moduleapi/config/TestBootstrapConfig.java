package partner42.moduleapi.config;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.config.BootstrapDataLoader;

@TestConfiguration
@Slf4j
@RequiredArgsConstructor
public class TestBootstrapConfig {
    @Value("${spring.jpa.hibernate.data-loader}")
    private Integer dataLoader;
    private final BootstrapDataLoader bootstrapDataLoader;

    @Transactional
    @PostConstruct
    public void initDB() {
        if (dataLoader.equals(2)){
            bootstrapDataLoader.createDefaultUsers();
            bootstrapDataLoader.createMatchCondition();
        }
    }
}

