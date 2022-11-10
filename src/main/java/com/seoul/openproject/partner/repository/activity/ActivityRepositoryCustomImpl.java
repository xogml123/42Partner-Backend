package com.seoul.openproject.partner.repository.activity;

import static com.seoul.openproject.partner.domain.model.activity.QActivity.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seoul.openproject.partner.domain.model.activity.QActivity;
import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.repository.article.ArticleSearch;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findSumScoreByMemberIdAndArticleSearch(Long memberId,
        ActivitySearch activitySearch) {
        return queryFactory.select(
                activity.score.sum()
            )
            .from(activity)
            .where(isContentCategory(activitySearch.getContentCategory()),
                isMemberId(memberId),
                goeStartTime(activitySearch.getStartTime()),
                ltEndTime(activitySearch.getEndTime())
            )
            .fetchFirst();
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
