package partner42.moduleapi.config.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedirectAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("${cors.frontend}")
    private String corsFrontend;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        String referer = corsFrontend;
//            request.getHeader(HttpHeaders.REFERER) == null ? corsFrontend
//                : request.getHeader("Referer");
//        log.info("referer: {}", request.getHeader(HttpHeaders.REFERER));
        response.sendRedirect(
            referer +
                "?login_success=" + false);
    }
}
