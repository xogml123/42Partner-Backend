package partner42.moduleapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JWTUtil {

//    public static createToken() {
//
//    }

    /**
     * 1. 토큰이 정상적인지 검증(위조, 만료 여부)
     * 2. Access Token인지 Refresh Token인지 구분
     * @param algorithm
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public static JWTInfo decodeToken(Algorithm algorithm, String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm).build();

        /**
         * WT 토큰 검증 실패하면 JWTVerificationException 발생
         * Throws:
         * AlgorithmMismatchException – if the algorithm stated in the token's header is not equal to the one defined in the JWTVerifier.
         * SignatureVerificationException – if the signature is invalid.
         * TokenExpiredException – if the token has expired.
         * InvalidClaimException – if a claim contained a different value than the expected one.
         */
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] authoritiesJWT = null;
        try{
            authoritiesJWT = decodedJWT.getClaim("authorities")
                .asArray(String.class);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Arrays.stream(authoritiesJWT).forEach(authority -> {
                authorities.add(new SimpleGrantedAuthority(authority));
            });
        } catch (JWTDecodeException e){
            //refresh token의 경우 authorities를 가지고 있지 않으므로 Exception 발생.
        }
        return JWTInfo.builder()
            .username(username)
            .authorities(authoritiesJWT)
            .build();
    }
    @Getter
    @Builder
    public static class JWTInfo{
        private final String username;
        private final String[] authorities;
    }
}
