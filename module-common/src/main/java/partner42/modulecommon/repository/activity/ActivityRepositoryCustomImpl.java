package partner42.modulecommon.repository.activity;


import static partner42.modulecommon.domain.model.activity.QActivity.activity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.match.ContentCategory;

@RequiredArgsConstructor
@Repository
public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ActivityMatchScore> findActivityMatchScoreByMemberIdAndArticleSearch(Long memberId,
        ActivitySearch activitySearch) {
        return queryFactory.select(activity.activityMatchScore)
            .from(activity)
            .where(isContentCategory(activitySearch.getContentCategory()),
                isMemberId(memberId),
                goeStartTime(activitySearch.getStartTime()),
                ltEndTime(activitySearch.getEndTime())
            )
            .fetch();
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null : activity.contentCategory.eq(contentCategory);
    }

    private BooleanExpression isMemberId(Long memberId) {
        return memberId == null ? null : activity.member.id.eq(memberId);
    }

    private BooleanExpression goeStartTime(LocalDateTime startTime) {
        return startTime == null ? null : activity.createdAt.goe(startTime);
    }

    private BooleanExpression ltEndTime(LocalDateTime endTime) {
        return endTime == null ? null : activity.createdAt.lt(endTime);
    }
}
