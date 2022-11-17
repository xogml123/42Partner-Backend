package partner42.moduleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.modulecommon.domain.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target="userId", source = "apiId")
    @Mapping(target="nickname", source = "member.nickname")
    UserDto entityToUserDto(User user);

    @Mapping(target="userId", source = "apiId")
    UserOnlyIdResponse entityToUserOnlyIdResponse(User user);
}