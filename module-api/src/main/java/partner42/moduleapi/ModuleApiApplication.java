package partner42.moduleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableJpaAuditing
@SpringBootApplication(
	scanBasePackages = {"partner42.moduleapi", "partner42.modulecommon"}
)
@EntityScan(basePackages = "partner42.modulecommon.domain")
@EnableJpaRepositories(basePackages =  "partner42.modulecommon.repository")
public class ModuleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleApiApplication.class, args);
	}

}
