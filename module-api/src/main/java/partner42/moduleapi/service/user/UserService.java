package partner42.moduleapi.service.user;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.mapper.UserMapper;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public UserDto findById(String userId) {

        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        return userMapper.entityToUserDto(user);
    }

    @Transactional
    public UserOnlyIdResponse updateEmail(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        user.updateEmail(userUpdateRequest.getEmail());
        return userMapper.entityToUserOnlyIdResponse(user);
    }
}
