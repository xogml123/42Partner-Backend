package partner42.moduleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.member.Member;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target="isAuthor", source = "articleMember.isAuthor")
    MemberDto entityToMemberDto(Member member, ArticleMember articleMember);
}
