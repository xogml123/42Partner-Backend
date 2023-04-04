package partner42.moduleapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import partner42.moduleapi.config.oauth2userservice.DefaultOAuth2UserServiceConfig;
import partner42.moduleapi.config.security.CustomAuthenticationEntryPoint;
import partner42.moduleapi.config.security.RedirectAuthenticationFailureHandler;
import partner42.moduleapi.config.security.RedirectAuthenticationSuccessHandler;
import partner42.moduleapi.service.user.CustomOAuth2UserService;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({CustomOAuth2UserService.class, DefaultOAuth2UserServiceConfig.class,
    CustomAuthenticationEntryPoint.class,
    RedirectAuthenticationSuccessHandler.class, RedirectAuthenticationFailureHandler.class})
public @interface WebMvcTestSecurityImport {
    Class<?>[] value() default {};
}
