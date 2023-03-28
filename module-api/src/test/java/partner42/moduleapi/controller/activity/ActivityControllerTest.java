package partner42.moduleapi.controller.activity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import partner42.moduleapi.config.security.SecurityConfig;
import partner42.moduleapi.controller.article.ArticleController;

@WebMvcTest(value = {ActivityController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
@AutoConfigureWebMvc
class ActivityControllerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void readMyActivityScore() {
    }
}