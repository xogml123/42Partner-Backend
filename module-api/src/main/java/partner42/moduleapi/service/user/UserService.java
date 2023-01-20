package partner42.moduleapi.service.user;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.user.AccessTokenResponse;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.mapper.UserMapper;
import partner42.moduleapi.util.JWTUtil;
import partner42.moduleapi.util.JWTUtil.JWTInfo;
import partner42.modulecommon.domain.model.user.Authority;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.user.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    @Value("${jwt.access-token-expire}")
    private String accessTokenExpire;
    @Value("${jwt.secret}")
    private String jwtSecret;


    private final MessageSource messageSource;
    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public UserDto findById(String userId, String username) {
        verifyUserIsNotMe(userId, username);
        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        return userMapper.entityToUserDto(user);
    }

    @Transactional
    public UserOnlyIdResponse updateEmail(String userId, UserUpdateRequest userUpdateRequest,
        String username) {
        verifyUserIsNotMe(userId, username);

        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        user.updateEmail(userUpdateRequest.getEmail());
        return userMapper.entityToUserOnlyIdResponse(user);
    }

    private void verifyUserIsNotMe(String userId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        User userTarget = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        if (!user.getUserRoles().stream()
            .map(UserRole::getRole)
            .map(Role::getValue)
            .collect(Collectors.toSet())
            .contains(RoleEnum.ROLE_ADMIN) &&
            !userTarget.equals(user)) {
            throw new InvalidInputException(ErrorCode.NOT_MINE);
        }
    }

    /**
     * 1. refreshToken 해독 하면서 유효성 및  token 만료 인지 검사(access_token과 다르게 만료이거나 유효성 없거나 동등하게 401로 처리) 2.
     * subject로 유저 찾아서 isAvailable 값 true인지 확인. 3. 1, 2중 하나라도 걸리면 401 4. 1, 2 모두 통과하면 access_token
     * 재발급
     *
     * @param refreshToken
     * @return
     */
    public AccessTokenResponse validateRefreshTokenAndCreateAccessToken(String refreshToken, String issuer) {

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTInfo jwtInfo = null;
        try {
            //JWT 토큰 검증 실패하면 JWTVerificationException 발생
            jwtInfo = JWTUtil.decodeToken(algorithm, refreshToken);
            log.debug(jwtInfo.toString());

            //refresh token 이 만료되었거나 적절하지 않은 경우 401
        } catch (JWTVerificationException jwtException) {
            log.debug("JWT Verification Failure : {}", jwtException.getMessage());
            throw new InvalidInputException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        User user = getUserFromJWTInfo(jwtInfo);
        List<String> authorities = user.getUserRoles().stream()
            .map(UserRole::getRole)
            .map(Role::getAuthorities)
            .flatMap(Set::stream)
            .map(Authority::getPermission)
            .collect(Collectors.toList());
        String accessToken = JWTUtil.createToken(issuer, user.getUsername(), accessTokenExpire, algorithm,
            authorities);
        return AccessTokenResponse.builder()
            .accessToken(accessToken)
            .build();
    }

    private User getUserFromJWTInfo(JWTInfo jwtInfo) {
        User user = userRepository.findByUsername(jwtInfo.getUsername())
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        if (!user.getIsAvailable()) {
            throw new InvalidInputException(ErrorCode.USER_TOKEN_NOT_AVAILABLE);
        }
        return user;
    }
}
