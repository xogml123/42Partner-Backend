package com.seoul.openproject.partner.service.user;


import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.domain.repository.user.UserRepository;
import com.seoul.openproject.partner.mapper.UserMapper;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;


    public User.UserDto findById(String userId) {

        User user = userRepository.findByApiId(userId).orElseThrow(() ->
            new EntityNotFoundException(userId + "에 해당하는 User가 없습니다."));
//        return User.UserDto.builder()
//            .userId(user.getApiId())
//            .oauth2Username(user.getOauth2Username())
//            .nickname(user.getMember().getNickname())
//            .email(user.getEmail())
//            .imageUrl(user.getImageUrl())
//            .slackEmail(user.getSlackEmail())
//            .build();
        return userMapper.entityToUserDto(user);
    }
}
