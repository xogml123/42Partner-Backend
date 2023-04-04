package partner42.moduleapi.controller.article;


import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.annotation.WebMvcTestSecurityImport;
import partner42.moduleapi.dto.EmailDto;
import partner42.moduleapi.dto.alarm.ResponseWithAlarmEventDto;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.match.MatchOnlyIdResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.service.article.ArticleService;
import partner42.moduleapi.service.user.CustomOAuth2UserService;
import partner42.modulecommon.config.kafka.AlarmEvent;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.utils.slack.SlackBotService;

@WebMvcTest(ArticleController.class)
@WebMvcTestSecurityImport
public class ArticleControllerWithSecurityTest {
    private MockMvc mockMvc;
    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ArticleService articleService;
    @MockBean
    private SlackBotService slackBotService;
    @MockBean
    private AlarmProducer alarmProducer;

    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockUser("username")
    void readOneArticle() throws Exception {
        //given
        String username = "username";
        String articleId = "articleId";
        //when
        mockMvc.perform(get("/api/articles/" + articleId))
            .andDo(print())
            .andExpect(status().isOk());
        //then
        verify(articleService).readOneArticle(username, articleId);
    }

    @Test
    void readOneArticle_whenUserIsNotAuthenticated_thenVerify() throws Exception {
        //given
        String articleId = "articleId";
        //when
        mockMvc.perform(get("/api/articles/" + articleId))
            .andDo(print())
            .andExpect(status().isOk());
        //then
        verify(articleService).readOneArticle(null, articleId);
    }

    @Test
    void readAllArticle_whenUserIsNotAuthenticated_thenExpectStatusOk() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/articles"))
            .andDo(print())
            .andExpect(status().isOk());
        //then
    }

    @Test
    void writeArticle_whenNotAuthenticated_then401() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON).content(
                    new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(articleDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.create"})
    void writeArticle_whenHasAuthority_then200() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/articles")
                .content(new ObjectMapper().registerModule(new JavaTimeModule())
                    .writeValueAsString(articleDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void writeArticle_whenNotHasAuthority_then403() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(post("/api/articles")
                .content(new ObjectMapper().registerModule(new JavaTimeModule())
                    .writeValueAsString(articleDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void updateArticle_whenNotAuthenticated_then401() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(put("/api/articles/**")
                .contentType(MediaType.APPLICATION_JSON).content(
                    new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(articleDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.update"})
    void updateArticle_whenHasAuthority_then200() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(put("/api/articles/**")
                .content(new ObjectMapper().registerModule(new JavaTimeModule())
                    .writeValueAsString(articleDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void updateArticle_whenNotHasAuthority_then403() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(LocalDate.now().plusDays(1))
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        mockMvc.perform(put("/api/articles/**")
                .content(new ObjectMapper().registerModule(new JavaTimeModule())
                    .writeValueAsString(articleDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteArticle_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(delete("/api/articles/**"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.delete"})
    void deleteArticle_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(delete("/api/articles/**"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void deleteArticle_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(delete("/api/articles/**"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void recoverableDeleteArticle_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(post("/api/articles/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.update"})
    void recoverableDeleteArticle_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(post("/api/articles/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void recoverableDeleteArticle_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(post("/api/articles/**/recoverable-delete"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void participateArticle_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(post("/api/articles/**/participate"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.update"})
    void participateArticle_whenHasAuthority_then200() throws Exception {

        given(articleService.participateArticle(any(), any())).willReturn(
            ResponseWithAlarmEventDto.<ArticleOnlyIdResponse>builder()
                .alarmEvent(new AlarmEvent()).build());
        mockMvc.perform(post("/api/articles/**/participate"))
            .andDo(print())
            .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "username")
    void participateArticle_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(post("/api/articles/**/participate"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void participateCancelArticle_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(post("/api/articles/**/participate-cancel"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.update"})
    void participateCancelArticle_whenHasAuthority_then200() throws Exception {

        given(articleService.participateCancelArticle(any(), any())).willReturn(
            ResponseWithAlarmEventDto.<ArticleOnlyIdResponse>builder()
                .alarmEvent(new AlarmEvent()).build());

        mockMvc.perform(post("/api/articles/**/participate-cancel"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void participateCancelArticle_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(post("/api/articles/**/participate-cancel"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void completeArticle_whenNotAuthenticated_then401() throws Exception {

        mockMvc.perform(post("/api/articles/**/complete"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"article.update"})
    void completeArticle_whenHasAuthority_then200() throws Exception {
        given(articleService.completeArticle(anyString(), anyString())).willReturn(
            EmailDto.<MatchOnlyIdResponse>builder()
                .alarmEventList(List.of())
                .emails(List.of()).build());
        mockMvc.perform(post("/api/articles/**/complete"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void completeArticle_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(post("/api/articles/**/complete"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

}