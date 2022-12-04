package partner42.modulecommon.repository.random;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import partner42.modulecommon.domain.model.random.MealRandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatch;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import partner42.modulecommon.domain.model.random.StudyRandomMatch;

public interface RandomMatchRepository extends JpaRepository<RandomMatch, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from MealRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findMealByCreatedAtBeforeAndIsExpiredAndMemberId(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);

    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from StudyRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findStudyByCreatedAtBeforeAndIsExpiredAndMemberId(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from MealRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findMealPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from StudyRandomMatch rm "
        + "where rm.member.id = :memberId "
        + "and rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<RandomMatch> findStudyPessimisticWriteByCreatedAtBeforeAndIsExpiredAndMemberId(
        @Param(value = "before") LocalDateTime before,
        @Param(value = "memberId") Long memberId,
        @Param(value  = "isExpired") boolean isExpired);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @EntityGraph(attributePaths = {"member"})
    @Query("select rm from MealRandomMatch rm "
        + "where rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<MealRandomMatch> findMealByCreatedAtBeforeAndIsExpired(
        @Param(value = "before") LocalDateTime before,
        @Param(value  = "isExpired") boolean isExpired);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select rm from StudyRandomMatch rm "
        + "where rm.createdAt > :before "
        + "and rm.isExpired = :isExpired")
    List<StudyRandomMatch> findStudyByCreatedAtBeforeAndIsExpired(
        @Param(value = "before") LocalDateTime before,
        @Param(value  = "isExpired") boolean isExpired);


    /**
     * 벌크성 수정 쿼리는 @Modifying 어노테이션을 사용해야 한다.
     * 또한 벌크성 수정 쿼리는 영속성 컨텍스트를 무시하고 실행되므로, 영속성 컨텍스트를 초기화해야 한다.
     * @param isExpired
     * @param after
     */
    @Modifying(clearAutomatically = true)
    @Query("update RandomMatch rm "
        + "set rm.isExpired = :isExpired "
        + "where rm.createdAt > :after ")
    void bulkUpdateIsExpired(@Param(value  = "isExpired") boolean isExpired,
        @Param(value = "after") LocalDateTime after);
}
