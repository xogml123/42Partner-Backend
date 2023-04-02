package partner42.moduleapi.config.security;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.moduleapi.error.ErrorResponse;
import partner42.moduleapi.util.JWTUtil;
import partner42.moduleapi.util.JWTUtil.JWTInfo;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;


/**
 * 1. OncePerRequestFilter를 활용하는 이유
 * Servlet이 다른 Servler을 dispatch하는 경우 FilterChain을 여러번 거치게 되는데
 * OnceOErRequestFilter를 사용하는 경우 무조건 한번만 거치게 된다.
 * https://stackoverflow.com/questions/13152946/what-is-onceperrequestfilter
 */
@Slf4j
@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    private static final String BEARER = "Bearer ";

    /**
     * 인증 시 Authorization header에 Bearer 토큰을 담아서 보내기 때문에
     * 이를 추출하여 토큰 검증을 진행한다.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = getToken(authorizationHeader);
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            JWTInfo jwtInfo = null;
            try {
                //JWT 토큰 검증 실패하면 JWTVerificationException 발생
                jwtInfo = JWTUtil.decodeToken(algorithm, token);
                log.debug(jwtInfo.toString());

                //access token이 만료된 경우 403과 응답 코드를 전달
            } catch(TokenExpiredException tokenExpiredException){
                log.debug(tokenExpiredException.getMessage());
                final ErrorResponse errorResponse = ErrorResponse.of(
                    ErrorCode.ACCESS_TOKEN_EXPIRED);
                setAccessTokenExpiredResponse(response, errorResponse);
                return ;
            } catch (JWTVerificationException jwtException) {
                log.debug("JWT Verification Failure : {}", jwtException.getMessage());
            }
            /**
             * Access Token인 경우 authorities가 존재하므로
             * SecurityContextHoler에 정보 저장.
             * SpringSecurity 에서 Authentication을 등록하지 않고 Custom Filter를 이용하여 등록해서 인지
             * 세션생성을 방지하는 옵션을 사용하였음에도 세션을 생성하여 반환함.
             * 만료된 토큰임에도 로그인이 풀리지 않아 세션을 생성하지 않도록 설정하려고 하였으나 실패함.
             * https://www.baeldung.com/spring-security-session
             * 이미 생성된 세션은 사용하지 않도록 SessionCreationPolicy NEVER->STATELESS로 변경하여 해결.
             */
            if (jwtInfo != null && jwtInfo.getAuthorities() != null) {
                SecurityContextHolder.getContext()
                    .setAuthentication(getAuthenticationTokenFromDecodedJwtInfo(jwtInfo));
            }
            //JWT 토큰이 없는 경우 일단 통과 시킴.
            //Security Filter chain에서 인증, 인가 여부가 필요한지에 따라
            //요청 처리여부가 결정됨..
        }
        //access token 만료를 제외 하면 모두 filter chain호출.
        filterChain.doFilter(request, response);
    }

    private void setAccessTokenExpiredResponse(HttpServletResponse response, ErrorResponse errorResponse)
        throws IOException {
        response.setStatus(errorResponse.getStatus());
        Map<String, String> body = new HashMap<>();
        body.put("message", errorResponse.getMessage());
        body.put("status",Integer.toString(errorResponse.getStatus()));
        body.put("code", errorResponse.getCode());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    /**
     * SecurityContextHolder에 Authentication 저장할 Authentication 정보를 저장.
     * @param jwtInfo
     */
    private UsernamePasswordAuthenticationToken getAuthenticationTokenFromDecodedJwtInfo(JWTInfo jwtInfo) {
        return  new UsernamePasswordAuthenticationToken(CustomAuthenticationPrincipal.of(
                User.of(jwtInfo.getUsername(),
                    null, null, null, null, null), null),
                null,
                Arrays.stream(jwtInfo.getAuthorities())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
    }

    private String getToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }
}
