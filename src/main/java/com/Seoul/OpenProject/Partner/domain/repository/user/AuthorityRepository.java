package com.Seoul.OpenProject.Partner.domain.repository.user;


import com.Seoul.OpenProject.Partner.domain.model.user.Authority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Optional<Authority> findByPermission(String s);
}