package partner42.modulecommon.repository.user;


import partner42.modulecommon.domain.model.user.Authority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Optional<Authority> findByPermission(String s);
}