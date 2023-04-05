package partner42.moduleapi.controller.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.Cookie;
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
import partner42.moduleapi.controller.random.RandomMatchController;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.moduleapi.service.user.UserService;
import partner42.moduleapi.util.JWTUtil;

@WebMvcTest(UserController.class)
@Import(WebMvcTestWithSecurityDefaultConfig.class)
class UserControllerWithSecurityTest {

    @MockBean
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;

    @MockBean
    private UserService userService;

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
    void getUserById_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(get("/api/users/**"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"user.read"})
    void getUserById_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(get("/api/users/**"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void getUserById_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(get("/api/users/**"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void updateUserEmail_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(patch("/api/users/**/email"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"user.update"})
    void updateUserEmail_whenHasAuthority_then200() throws Exception {

        mockMvc.perform(patch("/api/users/**/email")
                .content(new ObjectMapper().writeValueAsString(UserUpdateRequest.builder()
                    .email("email")
                    .build())).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username")
    void updateUserEmail_whenNotHasAuthority_then403() throws Exception {

        mockMvc.perform(patch("/api/users/**/email")
                .content(new ObjectMapper().writeValueAsString(UserUpdateRequest.builder()
                    .email("email")
                    .build())).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void getAccessTokenUsingRefreshToken_whenNotAuthenticated_then401() throws Exception {
        mockMvc.perform(post("/api/token/refresh"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAccessTokenUsingRefreshToken_whenWithCookie_then200() throws Exception {
        mockMvc.perform(post("/api/token/refresh")
                .cookie(new Cookie(JWTUtil.REFRESH_TOKEN, "value")))
            .andDo(print())
            .andExpect(status().isOk());
    }
    @Test
    void getAccessTokenUsingRefreshToken_whenWithCookieButWrongName_then401() throws Exception {
        mockMvc.perform(post("/api/token/refresh")
                .cookie(new Cookie("wrong", "value")))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

}