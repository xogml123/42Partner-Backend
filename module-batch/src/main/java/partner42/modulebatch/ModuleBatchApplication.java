package partner42.modulebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication(
    scanBasePackages = {"partner42.modulebatch", "partner42.modulecommon"}
)
@EntityScan(basePackages = "partner42.modulecommon.domain")
@EnableJpaRepositories(basePackages =  "partner42.modulecommon.repository")
public class ModuleBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleBatchApplication.class, args);
    }

}
