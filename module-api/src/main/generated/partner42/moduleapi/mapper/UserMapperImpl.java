package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-19T13:11:32+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto entityToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.userId( user.getApiId() );
        userDto.nickname( userMemberNickname( user ) );
        userDto.oauth2Username( user.getOauth2Username() );
        userDto.email( user.getEmail() );
        userDto.imageUrl( user.getImageUrl() );

        return userDto.build();
    }

    @Override
    public UserOnlyIdResponse entityToUserOnlyIdResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserOnlyIdResponse.UserOnlyIdResponseBuilder userOnlyIdResponse = UserOnlyIdResponse.builder();

        userOnlyIdResponse.userId( user.getApiId() );

        return userOnlyIdResponse.build();
    }

    private String userMemberNickname(User user) {
        if ( user == null ) {
            return null;
        }
        Member member = user.getMember();
        if ( member == null ) {
            return null;
        }
        String nickname = member.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }
}
