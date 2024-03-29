package partner42.moduleapi.controller.activity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import partner42.moduleapi.service.activity.ActivityService;

@WebMvcTest(ActivityController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
public class ActivityControllerWithSecurityTest {
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ActivityService activityService;

    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void readMyActivityScore_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(get("/api/activities/score"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"activity.read"})
    void readMyActivityScore_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(get("/api/activities/score"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void readMyActivityScore_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/activities/score"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

}
