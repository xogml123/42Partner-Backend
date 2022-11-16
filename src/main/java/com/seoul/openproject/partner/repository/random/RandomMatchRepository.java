package com.seoul.openproject.partner.repository.random;

import com.seoul.openproject.partner.domain.model.random.RandomMatch;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RandomMatchRepository extends JpaRepository<RandomMatch, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from MealRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isCanceled = :isCanceled")
    List<RandomMatch> findMealByCreatedAtBeforeAndIsCanceled(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isCanceled") boolean isCanceled);

    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from StudyRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isCanceled = :isCanceled")
    List<RandomMatch> findStudyByCreatedAtBeforeAndIsCanceled(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isCanceled") boolean isCanceled);
}
