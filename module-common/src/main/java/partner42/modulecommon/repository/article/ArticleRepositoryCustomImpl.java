package partner42.modulecommon.repository.article;


import static partner42.modulecommon.domain.model.article.QArticle.article;
import static partner42.modulecommon.domain.model.article.QArticleMember.articleMember;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;


@Slf4j
@RequiredArgsConstructor
@Repository
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {


    private final JPAQueryFactory queryFactory;

//    @PersistenceContext
//    private EntityManager em;
//    private JPAQueryFactory queryFactory;
//
//    public ArticleRepositoryCustomImpl() {
//        log.info("ArticleRepositoryCustomImpl");
//        this.queryFactory = new JPAQueryFactory(em);
//    }


    @Override
    public Slice<Article> findSliceByCondition(Pageable pageable, ArticleSearch condition) {
        JPAQuery<Article> query = queryFactory.select(article)
            .from(article)
            .where(
                isDeletedIsFalse(),
                isComplete(condition.getIsComplete()),
                isContentCategory(condition.getContentCategory()),
                isAnonymity(condition.getAnonymity())
            );
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(article.getType(),
                article.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.get(o.getProperty())));
        }
        List<Article> articles = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();
        boolean hasnext = false;
        if (articles.size() == pageable.getPageSize() + 1) {
            hasnext = true;
            articles.remove(articles.size() - 1);
        }
        return new SliceImpl<>(articles, pageable, hasnext);
    }

    private BooleanExpression isAnonymity(Boolean anonymity) {
        return anonymity == null ? null: article.anonymity.eq(anonymity);
    }

    private BooleanExpression isDeletedIsFalse() {
        return article.isDeleted.isFalse();
    }

    private BooleanExpression isComplete(Boolean isComplete) {

        return isComplete == null ? null : article.isComplete.eq(isComplete);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null: article.contentCategory.eq(contentCategory);
    }
}

