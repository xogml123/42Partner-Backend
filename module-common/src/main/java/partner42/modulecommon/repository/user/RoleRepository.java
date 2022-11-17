package partner42.modulecommon.repository.user;

import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(RoleEnum roleEnum);
}
