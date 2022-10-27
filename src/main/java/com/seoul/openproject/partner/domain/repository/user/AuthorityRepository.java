package com.seoul.openproject.partner.domain.repository.user;


import com.seoul.openproject.partner.domain.model.user.Authority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Optional<Authority> findByPermission(String s);
}