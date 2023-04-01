package partner42.modulecommon.repository.article;

import partner42.modulecommon.domain.model.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> , ArticleRepositoryCustom {

    void deleteByApiId(String apiId);

    @EntityGraph(attributePaths = {"articleMatchConditions"})
    Optional<Article> findEntityGraphArticleMatchConditionsByApiIdAndIsDeletedIsFalse(String articleId);

    @EntityGraph(attributePaths = {"articleMembers"})
    Optional<Article> findEntityGraphArticleMembersByApiIdAndIsDeletedIsFalse(String articleId);

    Optional<Article> findByApiIdAndIsDeletedIsFalse(String articleId);

    Optional<Article> findByApiId(String apiId);
}
