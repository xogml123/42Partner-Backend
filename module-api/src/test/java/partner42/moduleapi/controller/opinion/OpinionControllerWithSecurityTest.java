package partner42.moduleapi.controller.opinion;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.config.security.CustomAuthenticationEntryPoint;
import partner42.moduleapi.config.security.RedirectAuthenticationFailureHandler;
import partner42.moduleapi.config.security.RedirectAuthenticationSuccessHandler;
import partner42.moduleapi.controller.match.MatchController;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.dto.opinion.OpinionUpdateRequest;
import partner42.moduleapi.service.opinion.OpinionService;

@WebMvcTest(OpinionController.class)
@Import({DefaultOAuth2UserService.class, CustomAuthenticationEntryPoint.class,
    RedirectAuthenticationSuccessHandler.class, RedirectAuthenticationFailureHandler.class})
class OpinionControllerWithSecurityTest {
    @MockBean
    private OpinionService opinionService;
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
    void createOpinion_whenNotAuthenticated_then401() throws Exception {
        OpinionDto opinionDto = OpinionDto.builder()
            .parentId("parentId")
            .level(1)
            .content("content")
            .articleId("articleId")
            .build();
        mockMvc.perform(post("/api/opinions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"opinion.create"})
    void createOpinion_whenHasAuthority_then200() throws Exception {
        OpinionDto opinionDto = OpinionDto.builder()
            .parentId("parentId")
            .level(1)
            .content("content")
            .articleId("articleId")
            .build();
        mockMvc.perform(post("/api/opinions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionDto)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void createOpinion_whenNotHasAuthority_then403() throws Exception {

        OpinionDto opinionDto = OpinionDto.builder()
            .parentId("parentId")
            .level(1)
            .content("content")
            .articleId("articleId")
            .build();
        mockMvc.perform(post("/api/opinions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionDto)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }
    @Test
    void updateOpinion_whenNotAuthenticated_then401() throws Exception {
        OpinionUpdateRequest opinionUpdateRequest = OpinionUpdateRequest.builder()
            .content("content")
            .build();
        mockMvc.perform(put("/api/opinions/**")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionUpdateRequest)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"opinion.update"})
    void updateOpinion_whenHasAuthority_then200() throws Exception {
        OpinionUpdateRequest opinionUpdateRequest = OpinionUpdateRequest.builder()
            .content("content")
            .build();
        mockMvc.perform(put("/api/opinions/**")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionUpdateRequest)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void updateOpinion_whenNotHasAuthority_then403() throws Exception {

        OpinionUpdateRequest opinionUpdateRequest = OpinionUpdateRequest.builder()
            .content("content")
            .build();
        mockMvc.perform(put("/api/opinions/**")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(opinionUpdateRequest)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void recoverableDeleteOpinion_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(post("/api/opinions/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"opinion.update"})
    void recoverableDeleteOpinion_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(post("/api/opinions/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void recoverableDeleteOpinion_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(post("/api/opinions/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }


    @Test
    void getAllOpinionsInArticle() throws Exception {
        mockMvc.perform(get("/api/articles/**/opinions"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void getOneOpinion() throws Exception {
        mockMvc.perform(get("/api/opinions/**"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void completeDeleteOpinion_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(delete("/api/opinions/**"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"opinion.delete"})
    void completeDeleteOpinion_whenHasAuthority_then200() throws Exception {
        mockMvc.perform(delete("/api/opinions/**"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void completeDeleteOpinion_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(delete("/api/opinions/**"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

}