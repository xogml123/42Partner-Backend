package partner42.modulecommon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;


@SpringBootApplication
@Import({QuerydslConfig.class, Auditor.class})
class ModuleCommonApplicationTests {

	@Test
	void contextLoads() {
	}

}
