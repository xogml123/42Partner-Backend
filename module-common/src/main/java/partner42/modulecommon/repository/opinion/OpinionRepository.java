package partner42.modulecommon.repository.opinion;

import partner42.modulecommon.domain.model.opinion.Opinion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Optional<Opinion> findByApiId(String apiId);

    Optional<Opinion> findByApiIdAndIsDeletedIsFalse(String apiId);

    @EntityGraph(attributePaths = {"article", "memberAuthor"})
    List<Opinion> findAllEntityGraphArticleAndMemberAuthorByArticleApiIdAndIsDeletedIsFalse(String articleApiId);
}
