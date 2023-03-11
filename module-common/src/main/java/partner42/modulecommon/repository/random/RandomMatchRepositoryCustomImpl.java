package partner42.modulecommon.repository.random;

import static partner42.modulecommon.domain.model.member.QMember.member;
import static partner42.modulecommon.domain.model.random.QRandomMatch.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.random.RandomMatch;

@RequiredArgsConstructor
@Repository
public class RandomMatchRepositoryCustomImpl implements RandomMatchRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public List<RandomMatch> findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
        RandomMatchSearch randomMatchSearch) {
        return queryFactory.select(randomMatch)
            .from(randomMatch)
            .join(randomMatch.member, member)
            .where(isMemberIn(randomMatchSearch.getMemberId()),
                isExpired(randomMatchSearch.getIsExpired()),
                isCreatedAtAfter(randomMatchSearch.getCreatedAt()),
                isContentCategory(randomMatchSearch.getContentCategory()))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch();
    }

    @Override
    public List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
        RandomMatchSearch randomMatchSearch) {
        return queryFactory.select(randomMatch)
            .from(randomMatch)
            .join(randomMatch.member, member)
            .where(isMemberIn(randomMatchSearch.getMemberId()),
                isExpired(randomMatchSearch.getIsExpired()),
                isCreatedAtAfter(randomMatchSearch.getCreatedAt()),
                isContentCategory(randomMatchSearch.getContentCategory()))
            .fetch();
    }

    private BooleanExpression isMemberIn(Long memberId) {
        return memberId == null ? null : member.id.eq(memberId);
    }

    private BooleanExpression isExpired(Boolean isExpired) {
        return isExpired == null ? null: randomMatch.isExpired.eq(isExpired);
    }

    private BooleanExpression isCreatedAtAfter(LocalDateTime createdAt) {
        return createdAt == null ? null: randomMatch.createdAt.after(createdAt);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null : randomMatch.randomMatchCondition.contentCategory.eq(contentCategory);
    }
}
