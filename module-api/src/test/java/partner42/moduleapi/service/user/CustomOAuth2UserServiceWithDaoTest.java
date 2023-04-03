package partner42.moduleapi.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.moduleapi.service.user.CustomOAuth2UserServiceWithDaoTest.CustomOAuth2UserServiceWithDaoTestConfig;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.RoleRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.repository.user.UserRoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CustomOAuth2UserServiceWithDaoTestConfig.class,
    Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class, BCryptPasswordEncoder.class
})
class CustomOAuth2UserServiceWithDaoTest {

    @RequiredArgsConstructor
    @TestConfiguration
    static class CustomOAuth2UserServiceWithDaoTestConfig {
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final UserRoleRepository userRoleRepository;
        private final MemberRepository memberRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        @Bean
        @Qualifier("customOAuth2UserService")
        public CustomOAuth2UserService customOAuth2UserService(
            DefaultOAuth2UserService defaultOAuth2UserService) {
            return new CustomOAuth2UserService(userRepository,
                roleRepository, userRoleRepository, memberRepository, passwordEncoder);
        }

        @Bean
        @Qualifier("defaultOAuth2UserService")
        public DefaultOAuth2UserService defaultOAuth2UserService() {
            return new DefaultOAuth2UserService();
        }
    }
    @MockBean
    @Qualifier("defaultOAuth2UserService")
    private DefaultOAuth2UserService defaultOAuth2UserService;
    @Autowired
    @Qualifier("customOAuth2UserService")
    private CustomOAuth2UserService customOAuth2UserService;

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
                Map.of("id", "test-id", "login", "test-login", "email", "test-email", "image",
                    Map.of("link", "test-link")), "login"));
        //when
        OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);
        //then

        CustomAuthenticationPrincipal principal = (CustomAuthenticationPrincipal) oAuth2User;
        assertThat(oAuth2User.getAttributes()).containsExactly(entry("id", "test-id"),
            entry("login", "test-login"), entry("email", "test-email"),
            entry("image_url", "test-link"), entry("create_flag", true));
    }
}