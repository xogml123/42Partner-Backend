package partner42.moduleapi.controller.article;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.config.security.CustomAuthorizationFilter;
import partner42.moduleapi.config.security.SecurityConfig;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.service.article.ArticleService;
import partner42.moduleapi.service.user.CustomOAuth2UserService;
import partner42.modulecommon.utils.slack.SlackBotService;

//Spring Security설정 배제
@WebMvcTest(value = {ArticleController.class}
//    excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
//    }
)
@AutoConfigureWebMvc
class ArticleControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ArticleService articleService;

    @MockBean
    private SlackBotService slackBotService;

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
        ArticleReadOneResponse articleReadOneResponse = ArticleReadOneResponse.builder()
            .articleId(articleId)
            .build();

//        when(articleService.readOneArticle(any(), any())).thenReturn(articleReadOneResponse);
//        when(slackBotService.createSlackMIIM(List.of())
        //andExpect
        mockMvc.perform(get("/api/articles/" + articleId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.articleId").value(articleId))
            .andDo(print());

        //verify
//        verify(articleService).readOneArticle(username, articleId);
    }

    @Test
//    @WithMockUser(authorities = "article.create")
    void writeArticle() {
        //given
    }
}