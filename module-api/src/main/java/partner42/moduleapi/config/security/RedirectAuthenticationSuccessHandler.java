package partner42.moduleapi.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.IOException;
import java.time.Duration;
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
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.moduleapi.util.JWTUtil;

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
        String referer = corsFrontend;
        boolean createFlag = (boolean) (user.getAttributes().get("create_flag"));
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        String accessToken = JWTUtil.createToken(request.getRequestURL().toString(),
            user.getUsername(), accessTokenExpire, algorithm, user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList()));

        String refreshToken = JWTUtil.createToken(request.getRequestURL().toString(),
            user.getUsername(), refreshTokenExpire, algorithm);

        ResponseCookie cookie = ResponseCookie.from(JWTUtil.REFRESH_TOKEN, refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")      // path
            .maxAge(Duration.ofDays(15))
            .sameSite("None")  // sameSite
            .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(
            referer +
                "?access_token=" + accessToken +
                "&create_flag=" + createFlag +
                "&userId=" + user.getApiId());
    }


}
