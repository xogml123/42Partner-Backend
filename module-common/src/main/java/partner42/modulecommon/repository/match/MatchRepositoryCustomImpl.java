package partner42.modulecommon.repository.match;


import static partner42.modulecommon.domain.model.match.QMatch.match;
import static partner42.modulecommon.domain.model.match.QMatchMember.matchMember;
import static partner42.modulecommon.domain.model.member.QMember.member;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MethodCategory;

@RequiredArgsConstructor
@Repository
public class MatchRepositoryCustomImpl implements MatchRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Match> findAllMatchByMemberIdAndByMatchSearch(Long memberId, MatchSearch matchSearch, Pageable pageable){
        JPAQuery<Match> query = queryFactory.select(match)
            .from(match)
            .join(match.matchMembers, matchMember)
            .join(matchMember.member, member)
            .where(
                isMemberIn(memberId),
                isContentCategory(matchSearch.getContentCategory()),
                isMethodCategory(matchSearch.getMethodCategory())
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
        boolean hasnext = false;
        if (matches.size() == pageable.getPageSize() + 1) {
            hasnext = true;
            matches.remove(matches.size() - 1);
        }
        return new SliceImpl<>(matches, pageable, hasnext);
    }

    private BooleanExpression isMethodCategory(MethodCategory methodCategory) {
        return methodCategory == null ? null : match.methodCategory.eq(methodCategory);
    }

    private BooleanExpression isMemberIn(Long memberId) {
        if (memberId == null){
            throw new IllegalArgumentException("memberId is null");
        }
        return member.id.eq(memberId);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null: match.contentCategory.eq(contentCategory);
    }

}
