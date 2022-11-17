package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.member.Member;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-18T03:45:57+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberDto entityToMemberDto(Member member, ArticleMember articleMember) {
        if ( member == null && articleMember == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        if ( member != null ) {
            memberDto.nickname( member.getNickname() );
        }
        if ( articleMember != null ) {
            memberDto.isAuthor( articleMember.getIsAuthor() );
        }

        return memberDto.build();
    }
}
