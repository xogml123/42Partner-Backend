package partner42.modulecommon.repository.articlemember;

import static partner42.modulecommon.domain.model.activity.QActivity.activity;
import static partner42.modulecommon.domain.model.article.QArticle.article;
import static partner42.modulecommon.domain.model.article.QArticleMember.articleMember;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.article.ArticleMember;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ArticleMemberRepositoryCustomImpl implements ArticleMemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArticleMember> findByArticleApiId(String articleApiId) {
        return queryFactory.select(articleMember)
            .from(articleMember)
            .join(articleMember.article, article)
            .where(
                isArticleApiId(articleApiId)
            )
            .fetch();
    }

    private BooleanExpression isArticleApiId(String articleApiId) {
        return article.apiId.eq(articleApiId);
    }
}
