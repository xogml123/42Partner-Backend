package partner42.modulecommon.repository.match;

import static com.seoul.openproject.partner.domain.model.match.QMatch.match;
import static com.seoul.openproject.partner.domain.model.match.QMatchMember.matchMember;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
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
        JPAQuery<Match> query = queryFactory.select(QMatch.match).distinct()
            .from(QMatch.match)
            .join(QMatch.match.matchMembers, QMatchMember.matchMember).fetchJoin()
            .where(
                isMemberIn(memberId),
                isContentCategory(matchSearch.getContentCategory())
            );

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(QMatch.match.getType(),
                QMatch.match.getMetadata());
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
        return memberId == null ? null : QMatchMember.matchMember.member.id.eq(memberId);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null: QMatch.match.contentCategory.eq(contentCategory);
    }

}
