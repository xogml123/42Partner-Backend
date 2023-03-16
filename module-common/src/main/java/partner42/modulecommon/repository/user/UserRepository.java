package partner42.modulecommon.repository.user;

import partner42.modulecommon.domain.model.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"member"})
    Optional<User> findByApiId(String apiId);


}