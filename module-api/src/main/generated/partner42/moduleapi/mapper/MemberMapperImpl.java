package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.member.Member;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-08T05:06:19+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberDto articleMemberToMemberDto(Member member, ArticleMember articleMember, Boolean isMe) {
        if ( member == null && articleMember == null && isMe == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        if ( member != null ) {
            memberDto.nickname( member.getNickname() );
        }
        if ( articleMember != null ) {
            memberDto.isAuthor( articleMember.getIsAuthor() );
        }
        memberDto.isMe( isMe );

        return memberDto.build();
    }

    @Override
    public MemberDto matchMemberToMemberDto(Member member, MatchMember articleMember, Boolean isMe) {
        if ( member == null && articleMember == null && isMe == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        if ( member != null ) {
            memberDto.nickname( member.getNickname() );
        }
        if ( articleMember != null ) {
            memberDto.isAuthor( articleMember.getIsAuthor() );
        }
        memberDto.isMe( isMe );

        return memberDto.build();
    }
}
