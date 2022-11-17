package partner42.modulecommon.repository.member;

import static com.seoul.openproject.partner.domain.model.member.QMember.member;
import static com.seoul.openproject.partner.domain.model.user.QUser.user;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import partner42.modulecommon.domain.model.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findByUserId(Long userId){
        return jpaQueryFactory.select(QMember.member)
            .from(QUser.user)
            .join(QUser.user.member, QMember.member).fetchJoin()
            .where(QUser.user.id.eq(userId))
            .fetchOne();
    }
}
