package partner42.moduleapi.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;

@Slf4j
@Component
public class RedirectAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${cors.frontend}")
    private String corsFrontend;

    @Value("${jwt.access-token-expire}")
    private String accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private String refreshTokenExpire;
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     *     oauth 로그인 성공시 JWT Token 생성해서 리다이렉트 응답.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        CustomAuthenticationPrincipal user = (CustomAuthenticationPrincipal) authentication.getPrincipal();
        String referer =
            request.getHeader(HttpHeaders.REFERER) == null ? corsFrontend
                : request.getHeader("Referer");
        log.info("referer: {}", request.getHeader(HttpHeaders.REFERER));
        boolean createFlag = (boolean) (user.getAttributes().get("create_flag"));
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        String accessToken = JWT.create()
            .withSubject(user.getUsername())
            .withIssuer(request.getRequestURL().toString())
            .withExpiresAt(
                new Date(System.currentTimeMillis() + Integer.parseInt(accessTokenExpire)))

            .withClaim("authorities", user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .sign(algorithm);

        String refreshToken = JWT.create()
            .withSubject(user.getApiId())
            .withIssuer(request.getRequestURL().toString())
            .withExpiresAt(
                new Date(System.currentTimeMillis() + Integer.parseInt(refreshTokenExpire)))
            .sign(algorithm);
        response.sendRedirect(
            referer +
                "?access_token=" + accessToken +
                "&refresh_token=" + refreshToken +
                "&create_flag=" + createFlag +
                "&userId=" + user.getApiId());
    }


}
