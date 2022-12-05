package partner42.modulecommon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import partner42.modulecommon.config.auditing.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.utils.CreateTestDataUtils;


@SpringBootTest
@SpringBootApplication
@EnableJpaAuditing
@Import({CreateTestDataUtils.class, QuerydslConfig.class, Auditor.class})
class ModuleCommonApplicationTests {

	@Test
	void contextLoads() {
	}

}
