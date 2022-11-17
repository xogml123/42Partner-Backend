package partner42.modulecommon.repository.member;


import static partner42.modulecommon.domain.model.member.QMember.member;
import static partner42.modulecommon.domain.model.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.member.Member;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findByUserId(Long userId){
        return jpaQueryFactory.select(member)
            .from(user)
            .join(user.member, member).fetchJoin()
            .where(user.id.eq(userId))
            .fetchOne();
    }
}
