package com.seoul.openproject.partner.domain.repository.user;

import com.seoul.openproject.partner.domain.model.user.Role;
import com.seoul.openproject.partner.domain.model.user.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(RoleEnum roleEnum);
}
