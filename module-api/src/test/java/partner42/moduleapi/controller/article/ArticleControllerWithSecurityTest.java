package partner42.moduleapi.controller.article;


import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import partner42.moduleapi.config.security.SecurityConfig;
import partner42.moduleapi.service.article.ArticleService;
import partner42.modulecommon.utils.slack.SlackBotService;

@WebMvcTest(value = {ArticleController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
public class ArticleControllerWithSecurityTest {

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
}
