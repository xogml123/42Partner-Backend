package com.seoul.openproject.partner.repository.match;

import static com.seoul.openproject.partner.domain.model.match.QMatch.match;
import static com.seoul.openproject.partner.domain.model.match.QMatchMember.matchMember;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.domain.model.match.Match;
import com.seoul.openproject.partner.domain.model.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MatchRepositoryCustomImpl implements MatchRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Match> findAllFetchJoinMatchMemberId(Long memberId, MatchSearch matchSearch, Pageable pageable){
        JPAQuery<Match> query = queryFactory.select(match).distinct()
            .from(match)
            .join(match.matchMembers, matchMember).fetchJoin()
            .where(
                isMemberIn(memberId),
                isContentCategory(matchSearch.getContentCategory())
            );

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(match.getType(),
                match.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.get(o.getProperty())));
        }
        List<Match> matches = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();
        if (matches.size() == pageable.getPageSize() + 1) {
            matches.remove(matches.size() - 1);
        }
        return new SliceImpl<>(matches, pageable, matches.size() == pageable.getPageSize() + 1);
    }

    private BooleanExpression isMemberIn(Long memberId) {
        return memberId == null ? null : matchMember.member.id.eq(memberId);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null: match.contentCategory.eq(contentCategory);
    }

}
