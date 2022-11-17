package com.seoul.openproject.partner.service.user;

import com.seoul.openproject.partner.mapper.UserMapper;

//@SpringBootTest
//class UserServiceTest {
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Test
//    void findById(){
//        MatchTryAvailabilityJudge of = MatchTryAvailabilityJudge.of();
//        //given
//        User user = User.of("username", "encodedPassword", "email",
//            "oauth2Username",
//            "imageUrl", Member.of("nickname", of));
//        //when
//        UserDto userDto = userMapper.entityToUserDto(user);
//        //then
//        assertEquals(userDto.getNickname(), user.getMember().getNickname());
//        assertEquals(userDto.getOauth2Username(), user.getOauth2Username());
//        assertEquals(userDto.getEmail(), user.getEmail());
//        assertEquals(userDto.getImageUrl(), user.getImageUrl());
//        assertEquals(userDto.getUserId(), user.getApiId());
//
//    }
//
//
//}