package com.Seoul.OpenProject.Partner.domain.repository.user;

import com.Seoul.OpenProject.Partner.domain.model.user.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    List<User> findAll();

    Optional<User> findByUsername(String username);
    List<User> findAllByEmailAuth(boolean ch);
    Optional<User> findByApiId(String apiId);


    Optional<User> findFirstByApiId(String userAPI);

}