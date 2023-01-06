package partner42.moduleapi.config.security;

import java.net.MalformedURLException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SameSiteConfig implements WebMvcConfigurer {


    @Bean
    public CookieSerializer cookieSerializer() throws MalformedURLException {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("refresh-token");
        serializer.setSameSite("None");package partner42.moduleapi.config.security;

import java.net.MalformedURLException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

        @Configuration
        public class SameSiteConfig implements WebMvcConfigurer {


            @Bean
            public CookieSerializer cookieSerializer() throws MalformedURLException {
                DefaultCookieSerializer serializer = new DefaultCookieSerializer();
                serializer.setCookieName("refresh-token");
                serializer.setSameSite("None");
                serializer.setUseSecureCookie(true);
                serializer.setUseHttpOnlyCookie(true);
                return serializer;
            }
        }
        serializer.setUseSecureCookie(true);
        serializer.setUseHttpOnlyCookie(true);
        return serializer;
    }
}