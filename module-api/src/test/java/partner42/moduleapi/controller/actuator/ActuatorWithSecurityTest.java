package partner42.moduleapi.controller.actuator;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.config.WebMvcTestWithSecurityDefaultConfig;
import partner42.moduleapi.controller.alarm.AlarmController;
import partner42.moduleapi.controller.user.UserController;
import partner42.moduleapi.service.user.UserService;

@WebMvcTest(UserController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
public class ActuatorWithSecurityTest {
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    /**
     * 통합테스트가 아니기때문에 actuator관련 빈이 없어서 404가 나온다.
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {"actuator.read"})
    void actuator_whenHasAuthority_then404() throws Exception {
        mockMvc.perform(get("/actuator"))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    void actuator_whenHasAuthority_then401() throws Exception {
        mockMvc.perform(get("/actuator"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    void actuator_whenHasAuthority_then203() throws Exception {
        mockMvc.perform(get("/actuator"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }
}
