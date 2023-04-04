package partner42.moduleapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import partner42.moduleapi.config.security.CustomAuthenticationEntryPoint;
import partner42.moduleapi.config.security.RedirectAuthenticationFailureHandler;
import partner42.moduleapi.config.security.RedirectAuthenticationSuccessHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({DefaultOAuth2UserService.class, CustomAuthenticationEntryPoint.class,
    RedirectAuthenticationSuccessHandler.class, RedirectAuthenticationFailureHandler.class})
public @interface WebMvcTestSecurityImport {
    Class<?>[] value() default {};
}
