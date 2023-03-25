package partner42.moduleapi;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.config.BootstrapDataLoader;

@TestConfiguration
@Slf4j
@RequiredArgsConstructor
public class TestBootstrapConfig {
    private final BootstrapDataLoader bootstrapDataLoader;
    @Transactional
    @PostConstruct
    public void initDB() {
        bootstrapDataLoader.createDefaultUsers();
        bootstrapDataLoader.createMatchCondition();
    }
}

