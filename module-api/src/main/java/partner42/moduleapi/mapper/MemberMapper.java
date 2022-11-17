package partner42.moduleapi.mapper;

import com.seoul.openproject.partner.domain.model.article.ArticleMember;
import com.seoul.openproject.partner.domain.model.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target="isAuthor", source = "articleMember.isAuthor")
    Member.MemberDto entityToMemberDto(Member member, ArticleMember articleMember);
}
