package partner42.moduleapi.config.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
 * 스프링 시큐리티 인증 실패 시, 401 응답을 보내기 위한 커스텀 클래스
 * 설정 하지 않으면 defalut로 설정도된 Spring Security 인증 실패 html 페이지를 반환함.
 *
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message - {}", e.getMessage());

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}