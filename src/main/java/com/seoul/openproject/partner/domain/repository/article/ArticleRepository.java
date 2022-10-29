package com.seoul.openproject.partner.domain.repository.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    void deleteByApiId(String apiId);

    @EntityGraph(attributePaths = {"articleMatchConditions"})
    Optional<Article> findByApiId(String articleId);
}
