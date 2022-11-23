package partner42.moduleapi.service.user;


import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.mapper.UserMapper;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

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
}
