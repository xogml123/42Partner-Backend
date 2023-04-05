package partner42.moduleapi.controller.random;

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
import partner42.moduleapi.controller.opinion.OpinionController;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.dto.random.RandomMatchCancelRequest;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

@WebMvcTest(RandomMatchController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
class RandomMatchControllerWithSecurityTest {
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    @MockBean
    private RandomMatchService randomMatchService;
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
    void applyRandomMatch_whenNotAuthenticated_then401() throws Exception {
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .wayOfEatingList(List.of())
                .placeList(List.of()).build())
            .build();
        mockMvc.perform(post("/api/random-matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"random-match.create"})
    void applyRandomMatch_whenHasAuthority_then201() throws Exception {
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .wayOfEatingList(List.of())
                .placeList(List.of()).build())
            .build();
        mockMvc.perform(post("/api/random-matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "username")
    void applyRandomMatch_whenNotHasAuthority_then403() throws Exception {

        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .wayOfEatingList(List.of())
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .placeList(List.of()).build())
            .build();
        mockMvc.perform(post("/api/random-matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }


    @Test
    void cancelRandomMatch_whenNotAuthenticated_then401() throws Exception {
        RandomMatchCancelRequest randomMatchCancelRequest = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/random-matches/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchCancelRequest)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"random-match.delete"})
    void cancelRandomMatch_whenHasAuthority_then200() throws Exception {
        RandomMatchCancelRequest randomMatchCancelRequest = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/random-matches/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchCancelRequest)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void cancelRandomMatch_whenNotHasAuthority_then403() throws Exception {

        RandomMatchCancelRequest randomMatchCancelRequest = RandomMatchCancelRequest.builder()
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/random-matches/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(randomMatchCancelRequest)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void checkRandomMatchExist_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(get("/api/random-matches/mine"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"random-match.read"})
    void checkRandomMatchExist_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(get("/api/random-matches/mine"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void checkRandomMatchExist_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/random-matches/mine"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void countRandomMatchNotExpired_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(get("/api/random-matches/members/count"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void readRandomMatchCondition_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(get("/api/random-matches/condition/mine"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"random-match.read"})
    void readRandomMatchCondition_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(get("/api/random-matches/condition/mine"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void readRandomMatchCondition_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/random-matches/condition/mine"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

}