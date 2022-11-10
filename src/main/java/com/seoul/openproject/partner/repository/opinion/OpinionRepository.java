package com.seoul.openproject.partner.repository.opinion;

import com.seoul.openproject.partner.domain.model.opnion.Opinion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Optional<Opinion> findByApiId(String apiId);

    @EntityGraph(attributePaths = {"article", "memberAuthor"})
    @Query("select o from Opinion o where o.article.apiId LIKE :articleApiId and o.isDeleted = false")
    List<Opinion> findAllByArticleApiIdAndIsDeletedIsFalse(@Param("articleApiId") String articleApiId);
}
