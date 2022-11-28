package partner42.modulecommon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.utils.CreateUserUtils;


@SpringBootTest
@SpringBootApplication
@Import(CreateUserUtils.class)
class ModuleCommonApplicationTests {

	@Test
	void contextLoads() {
	}
}
