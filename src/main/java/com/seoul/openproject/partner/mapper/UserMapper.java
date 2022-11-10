package com.seoul.openproject.partner.mapper;

import com.seoul.openproject.partner.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target="userId", source = "apiId")
    @Mapping(target="nickname", source = "member.nickname")
    User.UserDto entityToUserDto(User user);

    @Mapping(target="userId", source = "apiId")
    User.UserOnlyIdResponse entityToUserOnlyIdResponse(User user);
}