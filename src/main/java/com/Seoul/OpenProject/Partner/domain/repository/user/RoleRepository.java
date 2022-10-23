package com.Seoul.OpenProject.Partner.domain.repository.user;

import com.Seoul.OpenProject.Partner.domain.model.user.Role;
import com.Seoul.OpenProject.Partner.domain.model.user.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(RoleEnum roleEnum);
}
