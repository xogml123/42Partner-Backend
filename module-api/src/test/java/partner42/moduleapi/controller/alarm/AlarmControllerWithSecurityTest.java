package partner42.moduleapi.controller.alarm;

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
import partner42.moduleapi.service.alarm.AlarmService;

@WebMvcTest(AlarmController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
class AlarmControllerWithSecurityTest {
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    private MockMvc mockMvc;
    @MockBean
    private AlarmService alarmService;
    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void readAllAlarms_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(get("/api/alarms"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"alarm.read"})
    void readAllAlarms_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(get("/api/alarms"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void readAllAlarms_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/alarms"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void alarmSubscribe_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(get("/api/alarm/subscribe"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"alarm.read"})
    void alarmSubscribe_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(get("/api/alarm/subscribe"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void alarmSubscribe_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/alarm/subscribe"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

}