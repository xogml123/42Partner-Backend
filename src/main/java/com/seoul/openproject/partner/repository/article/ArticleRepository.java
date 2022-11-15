package com.seoul.openproject.partner.repository.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleOnlyIdResponse;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ArticleRepository extends JpaRepository<Article, Long> , ArticleRepositoryCustom {

    void deleteByApiId(String apiId);

    //distinct 안하면
    @EntityGraph(attributePaths = {"articleMatchConditions"})
    Optional<Article> findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(String articleId);

    @EntityGraph(attributePaths = {"articleMembers"})
    Optional<Article> findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(String articleId);

    @EntityGraph(attributePaths = {"articleMembers"})
    Optional<Article> findDistinctFetchArticleMembersByApiId(String articleId);

    Optional<Article> findByApiIdAndIsDeletedIsFalse(String articleId);

    Optional<Article> findByApiId(String articleId);

//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
//    Optional<Article> findPessimisticWriteLockByApiId(String articleId);

}
