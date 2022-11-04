package com.seoul.openproject.partner.repository.article;

import static com.seoul.openproject.partner.domain.model.article.QArticle.article;
import static com.seoul.openproject.partner.domain.model.article.QArticleMember.articleMember;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seoul.openproject.partner.domain.model.article.Article;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Article> findSliceByCondition(@Parameter() Pageable pageable, ArticleSearch condition) {
        JPAQuery<Article> query = queryFactory.select(article).distinct()
            .from(article)
            .join(article.articleMembers, articleMember).fetchJoin()
            .where(
                isDeletedIsFalse(),
                isComplete(condition.getIsMatched())
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
        articles.remove(articles.size() - 1);
        return new SliceImpl<>(articles, pageable, articles.size() == pageable.getPageSize() + 1);
    }

    private BooleanExpression isDeletedIsFalse() {
        return article.isDeleted.isFalse();
    }

    private BooleanExpression isComplete(Boolean isMatched) {

        return isMatched == null ? null :article.complete.eq(isMatched);
    }
}

