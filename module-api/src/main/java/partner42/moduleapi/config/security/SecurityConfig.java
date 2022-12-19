package partner42.moduleapi.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import partner42.moduleapi.dto.ErrorResponseDto;
import partner42.moduleapi.dto.LoginResponseDto;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.modulecommon.domain.model.user.UserRole;

// spring security 필터를 스프링 필터체인에 동록
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
//Secured, PrePost 어노테이션 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DefaultOAuth2UserService oAuth2UserService;

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final ObjectMapper objectMapper;

    private final CustomAuthorizationFilter customAuthorizationFilter;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    @Value("${cors.frontend}")
    private String corsFrontend;

    @Value("${jwt.access-token-expire}")
    private String accessTokenExpire;

    @Value("${jwt.secret}")
    private String jwtSecret;

//    @Bean
//    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
//        return new JdbcOAuth2AuthorizedClientService(jdbc, clientRegistrationRepository);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.cors().configurationSource(corsConfigurationSource());
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        //session생성하지 않음. -> jwt 사용.
        //https://www.baeldung.com/spring-security-session
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //http 요청을 하더라도 https요청으로 하도록 브라우저에게 알려주는 헤더
        //초기개발시에만 비활성화
//        http.headers()
//            .httpStrictTransportSecurity().disable();
        http.authorizeRequests(
            authorize -> authorize
                //swagger
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/swagger-resources").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                //UserController
                .antMatchers(HttpMethod.GET, "/api/users/*").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/users/*/email").authenticated()
                //RandomMatchController
                .antMatchers(HttpMethod.POST, "/api/random-matches").authenticated()
                .antMatchers(HttpMethod.POST, "/api/random-matches/mine").authenticated()
                .antMatchers(HttpMethod.GET, "/api/random-matches/mine").authenticated()
                .antMatchers(HttpMethod.GET, "/api/random-matches/condition/mine").authenticated()

                //OpinionController
                .antMatchers(HttpMethod.POST, "/api/opinions").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/opinions/*").authenticated()
                .antMatchers(HttpMethod.POST, "/api/opinions/*/recoverable-delete").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/opinions/*").authenticated()
                //MatchController
                .antMatchers(HttpMethod.GET, "/api/matches").authenticated()
                .antMatchers(HttpMethod.GET, "/api/matches/*").authenticated()
                .antMatchers(HttpMethod.POST, "/api/matches/*/review").authenticated()
                //ArticleController
                .antMatchers(HttpMethod.POST, "/api/articles").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/articles/*").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/articles/*").authenticated()
                .antMatchers(HttpMethod.POST, "/api/articles/*/recoverable-delete").authenticated()
                .antMatchers(HttpMethod.POST, "/api/articles/*/participate").authenticated()
                .antMatchers(HttpMethod.POST, "/api/articles/*/participate-cancel").authenticated()
                .antMatchers(HttpMethod.POST, "/api/articles/*/complete").authenticated()
                //ActivityController
                .antMatchers(HttpMethod.GET, "/api/activities/score").authenticated()
                .antMatchers("/**").permitAll()
        );

            /*
                callback(redirect) URI: /login/oauth2/code/authclient - 아예 정해진거라 못바꿈
                login URI: /oauth2/authorization/authclient - 설정을 하면 바꿀 수 있을 것 같음.
             */



            http.oauth2Login()
            .userInfoEndpoint()
            .userService(oAuth2UserService)
            .and()
            .successHandler(authenticationSuccessHandler)
            .failureHandler((req, response, auth) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                    ErrorResponseDto.builder()
                        .message("로그인에 실패하였습니다.")
                        .build()));
            })
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/api/security/logout"))
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK);
            });

        http.formLogin(loginConfigurer ->
            loginConfigurer
                .successHandler((req, res, auth) -> {
                    CustomAuthenticationPrincipal user = (CustomAuthenticationPrincipal) auth.getPrincipal();
                    LoginResponseDto body = new LoginResponseDto();
                    res.setStatus(200);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("utf-8");
//                        addSameSiteCookieAttribute(res);
                    body.setUserId(user.getApiId());
                    Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
                    String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withIssuer(req.getRequestURL().toString())
                        .withExpiresAt(
                            new Date(System.currentTimeMillis() + Integer.parseInt(accessTokenExpire)))

                        .withClaim("authorities", user.getAuthorities().stream()
                            .map(SimpleGrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                        .sign(algorithm);
                    body.setAccessToken(accessToken);
                    res.getWriter().write(objectMapper.writeValueAsString(body));
                })
                .failureHandler((req, res, e) -> {
                    res.setStatus(401);
                })
                .usernameParameter("username")
                .loginProcessingUrl("/api/auth/login"));

    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring();
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(corsFrontend, "https://www.42partner.com"));
        configuration.setAllowedMethods(List.of("HEAD",
            "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    //인증 방식 수동 지정. userDetailsService, passwordEncoder 하나일때는 상관없음.
//    @Override
//    protected void configure(AuthenticationManagerBuilder security) throws Exception {
//        security.userDetailsService(new JpaUserDetailService(userRepository)).passwordEncoder(bCryptPasswordEncoder());
//    }

//    private void addSameSiteCookieAttribute(HttpServletResponse response) {
//        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
//        boolean firstHeader = true;
//        // there can be multiple Set-Cookie attributes
//        for (String header : headers) {
//            if (firstHeader) {
//                response.setHeader(HttpHeaders.SET_COOKIE,
//                    String.format("%s; %s", header, "SameSite=None"));
//                firstHeader = false;
//                continue;
//            }
//            response.addHeader(HttpHeaders.SET_COOKIE,
//                String.format("%s; %s", header, "SameSite=None"));
//        }
//    }

}
