package partner42.moduleapi.controller.random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 통합 테스트
 * 통합 테스트를 위해서는 MockMvc를 빌더로 생성해야함.
 * WithMockUser를 통해 사용자가 요청하는 것 요청.
 */
@SpringBootTest
class RandomMatchControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    @Test
    @WithMockUser(authorities = "random-match.create")
    void applyRandomMatch() {
        //given
//        mvc = MockMvcBuilders.webAppContextSetup(context)
//            .apply(springSecurity())
//            .build();
    }

    @Test
    void cancelRandomMatch() {
    }
}