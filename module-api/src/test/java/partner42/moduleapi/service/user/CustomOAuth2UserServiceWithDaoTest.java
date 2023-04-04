package partner42.moduleapi.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.config.ServiceWithDAOTestDefaultConfig;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.config.oauth2userservice.DefaultOAuth2UserServiceConfig;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CustomOAuth2UserService.class, DefaultOAuth2UserServiceConfig.class,
    ServiceWithDAOTestDefaultConfig.class,
})
class CustomOAuth2UserServiceWithDaoTest {

    @MockBean
    @Qualifier("defaultOAuth2UserService")
    private DefaultOAuth2UserService defaultOAuth2UserService;
    @Autowired
    @Qualifier("customOAuth2UserService")
    private DefaultOAuth2UserService customOAuth2UserService;

    private ClientRegistration clientRegistration = ClientRegistration
        .withRegistrationId("test-client")
        .clientId("test-id")
        .clientSecret("test-secret")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("http://example.com")
        .scope("read", "write")
        .authorizationUri("http://example.com/oauth2/authorize")
        .tokenUri("http://example.com/oauth2/token")
        .userInfoUri("http://example.com/userinfo")
        .userNameAttributeName("id")
        .clientName("Test Client")
        .build();

    @Test
    void loadUser() {
        //given
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "test-token",
            Instant.now(),
            Instant.now().plusSeconds(3600));

        OAuth2UserRequest userRequest = new OAuth2UserRequest(
            clientRegistration,
            accessToken);
        //given
        given(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
            .willReturn(new DefaultOAuth2User(null,
                Map.of("id", 3, "login", "test-login", "email", "test-email", "image",
                    Map.of("link", "test-link")), "login"));
        //when
        OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);
        //then
        CustomAuthenticationPrincipal principal = (CustomAuthenticationPrincipal) oAuth2User;
        assertThat(principal.getAttributes()).containsOnly(entry("id", "3"),
            entry("login", "test-login"), entry("email", "test-email"),
            entry("image_url", "test-link"), entry("create_flag", true));
        assertThat(principal).extracting(
            CustomAuthenticationPrincipal::getUsername
        ).isEqualTo(
            "test-email"
        );
    }

    @Test
    void loadUser_whenUserAlreadySignUp_thenFlag() {
        //given
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "test-token",
            Instant.now(),
            Instant.now().plusSeconds(3600));

        OAuth2UserRequest userRequest = new OAuth2UserRequest(
            clientRegistration,
            accessToken);
        //given
        given(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
            .willReturn(new DefaultOAuth2User(null,
                Map.of("id", 3, "login", "test-login", "email", "test-email", "image",
                    Map.of("link", "test-link")), "login"));
        //when
        customOAuth2UserService.loadUser(userRequest);
        OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);

        //then
        CustomAuthenticationPrincipal principal = (CustomAuthenticationPrincipal) oAuth2User;
        assertThat(principal.getAttributes()).contains(entry("create_flag", false));

    }
}