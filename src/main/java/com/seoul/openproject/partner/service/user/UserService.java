package com.seoul.openproject.partner.service.user;


import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.domain.model.user.User.UserDto;
import com.seoul.openproject.partner.domain.model.user.User.UserUpdateRequest;
import com.seoul.openproject.partner.error.exception.ErrorCode;
import com.seoul.openproject.partner.error.exception.NoEntityException;
import com.seoul.openproject.partner.repository.user.UserRepository;
import com.seoul.openproject.partner.mapper.UserMapper;
import java.util.Locale;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public User.UserDto findById(String userId) {

        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        return userMapper.entityToUserDto(user);
    }

    @Transactional
    public User.UserOnlyIdResponse updateEmail(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        user.updateEmail(userUpdateRequest.getEmail());
        return userMapper.entityToUserOnlyIdResponse(user);
    }
}
