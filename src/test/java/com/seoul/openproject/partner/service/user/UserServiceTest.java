package com.seoul.openproject.partner.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.domain.model.user.User.UserDto;
import com.seoul.openproject.partner.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void findById(){
        //given
        User user = User.createDefaultUser("username", "encodedPassword", "email",
            "oauth2Username",
            "imageUrl", Member.of("nickname"));
        //when
        UserDto userDto = userMapper.entityToUserDto(user);
        //then
        assertEquals(userDto.getNickname(), user.getMember().getNickname());
        assertEquals(userDto.getOauth2Username(), user.getOauth2Username());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getImageUrl(), user.getImageUrl());
        assertEquals(userDto.getSlackEmail(), user.getSlackEmail());
        assertEquals(userDto.getUserId(), user.getApiId());
    }
}