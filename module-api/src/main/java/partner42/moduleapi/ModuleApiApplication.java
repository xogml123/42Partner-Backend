package partner42.moduleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(
	scanBasePackages = {"partner42.moduleapi", "partner42.modulecommon"}
)
public class ModuleApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ModuleApiApplication.class, args);
	}
}
