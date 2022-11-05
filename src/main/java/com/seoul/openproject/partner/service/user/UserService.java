package com.seoul.openproject.partner.service.user;


import com.seoul.openproject.partner.domain.model.user.User;
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
            new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{userId, User.class.toString()}, Locale.KOREAN)));

        return userMapper.entityToUserDto(user);
    }
}
