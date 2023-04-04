package partner42.moduleapi.controller.article;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import partner42.moduleapi.config.security.SecurityConfig;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.service.article.ArticleService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.utils.slack.SlackBotService;

/**
 * Spring Security설정 배제하고 주로 Validation 검증
 */
@WebMvcTest(value = {ArticleController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private SlackBotService slackBotService;
    @MockBean
    private AlarmProducer alarmProducer;

    @Test
    void readAllArticle_pageable_paramPerformWell() throws Exception {
        //given
        //andExpect
        mockMvc.perform(get("/api/articles/**")
                .param("isComplete", "true")
                .param("contentCategory", ContentCategory.MEAL.toString())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "id,DESC"))
            .andExpect(status().isOk())
            .andDo(print());
        //verify
    }

    @Test
    void writeArticle_whenArticleDtoDateIsBeforeToday_then400() throws Exception{
        //given
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(yesterday)
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
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void writeArticle_whenArticleDtoDateIsBeforeToday_then201() throws Exception{
        //given
        LocalDate today = LocalDate.now();

        ArticleDto articleDto = ArticleDto.builder()
            .title("title")
            .date(today)
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
            .andExpect(status().isOk());
    }
    @Test
    @WithMockUser
    void writeArticle_ArticleDtoParticipantNumMaxBoundaryValidation() throws Exception{
        //given
        LocalDate today = LocalDate.now();

        int participantNumMax = 0;
        ArticleDto articleDtoUnderMin = ArticleDto.builder()
            .title("title")
            .date(today)
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(participantNumMax)
            .contentCategory(ContentCategory.MEAL)
            .build();
        int participantNumMax1 = 20;
        ArticleDto articleDtoMax = ArticleDto.builder()
            .title("title")
            .date(today)
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(participantNumMax1)
            .contentCategory(ContentCategory.MEAL)
            .build();
        int participantNumMax2 = 21;
        ArticleDto articleDtoOverMax = ArticleDto.builder()
            .title("title")
            .date(today)
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(List.of())
                .timeOfEatingList(List.of())
                .wayOfEatingList(List.of())
                .build())
            .content("content")
            .anonymity(false)
            .participantNumMax(participantNumMax2)
            .contentCategory(ContentCategory.MEAL)
            .build();
        //then
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON).content(
                    new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(articleDtoMax)))
            .andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON).content(
                    new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(articleDtoUnderMin)))
            .andDo(print())
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON).content(
                    new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(articleDtoOverMax)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}