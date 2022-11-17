package partner42.modulecommon.repository.user;

import partner42.modulecommon.domain.model.user.UserRole;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    //lazy 인 대상이 fetch join편하게 할 수 있게
//  @EntityGraph(attributePaths = {"addresses"}, type = EntityGraph.EntityGraphType.LOAD)
    @EntityGraph(attributePaths = "user")
    @Query("SELECT ur FROM UserRole ur WHERE ur.createdAt <= :date ORDER BY ur.createdAt DESC")
    List<UserRole> findAllByDate(@Param("date") LocalDateTime date);

    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.user u " +
            "WHERE ur.createdAt <= :date " +
            "ORDER BY ur.createdAt DESC")
    List<UserRole> findAllByDateAndAttendStatus(
            @Param("date") LocalDateTime date);

    List<UserRole> findAllByCreatedAt(LocalDateTime date);
}
