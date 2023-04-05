package partner42.moduleapi.controller.match;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.annotation.WebMvcTestSecurityImport;
import partner42.moduleapi.config.WebMvcTestWithSecurityDefaultConfig;
import partner42.moduleapi.config.security.CustomAuthenticationEntryPoint;
import partner42.moduleapi.config.security.RedirectAuthenticationFailureHandler;
import partner42.moduleapi.config.security.RedirectAuthenticationSuccessHandler;
import partner42.moduleapi.controller.alarm.AlarmController;
import partner42.moduleapi.dto.match.MatchReviewRequest;
import partner42.moduleapi.service.match.MatchService;
import partner42.moduleapi.service.user.CustomOAuth2UserService;

@WebMvcTest(MatchController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
class MatchControllerWithSecurityTest {
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    @MockBean
    private MatchService matchService;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void readMyMatches_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(get("/api/matches"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"match.read"})
    void readMyMatches_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(get("/api/matches"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void readMyMatches_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/matches"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }
    @Test
    void readOneMatch_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(get("/api/matches/**"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"match.read"})
    void readOneMatch_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(get("/api/matches/**"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void readOneMatch_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/matches/**"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void makeReview_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(post("/api/matches/**/review"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"match.update", "activity.create"})
    void makeReview_whenHasAuthority_then200() throws Exception {
        MatchReviewRequest mrr = MatchReviewRequest.builder()
            .matchId("matchId")
            .memberReviewDtos(List.of())
            .build();
        mockMvc.perform(post("/api/matches/**/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mrr)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void makeReview_whenNotHasAuthority_then403() throws Exception {

        MatchReviewRequest mrr = MatchReviewRequest.builder()
            .matchId("matchId")
            .memberReviewDtos(List.of())
            .build();
        mockMvc.perform(post("/api/matches/**/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mrr)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }
}