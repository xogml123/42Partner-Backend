package com.seoul.openproject.partner.repository.member;

import static com.seoul.openproject.partner.domain.model.member.QMember.member;
import static com.seoul.openproject.partner.domain.model.user.QUser.user;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seoul.openproject.partner.domain.model.member.Member;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
