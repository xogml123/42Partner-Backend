package partner42.modulecommon.repository.random;

import org.springframework.data.jpa.repository.Modifying;
import partner42.modulecommon.domain.model.random.RandomMatch;
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
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findMealByCreatedAtBeforeAndIsExpired(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);

    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from StudyRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findStudyByCreatedAtBeforeAndIsExpired(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);


    @Modifying(clearAutomatically = true)
    @Query("update RandomMatch rm "
        + "set rm.isExpired = :isExpired "
        + "where rm.createdAt > :after ")
    void bulkUpdateIsexpired(@Param(value  = "isExpired") boolean isExpired,
        @Param(value = "after") LocalDateTime after);
}
