package partner42.moduleapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import partner42.moduleapi.config.oauth2userservice.DefaultOAuth2UserServiceConfig;
import partner42.moduleapi.config.security.CustomAuthenticationEntryPoint;
import partner42.moduleapi.config.security.RedirectAuthenticationFailureHandler;
import partner42.moduleapi.config.security.RedirectAuthenticationSuccessHandler;
import partner42.moduleapi.service.user.CustomOAuth2UserService;

@TestConfiguration
@Import({CustomOAuth2UserService.class, DefaultOAuth2UserServiceConfig.class,
    CustomAuthenticationEntryPoint.class,
    RedirectAuthenticationSuccessHandler.class, RedirectAuthenticationFailureHandler.class})
public class WebMvcTestWithSecurityDefaultConfig {

}
