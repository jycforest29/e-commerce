package com.jycforest29.commerce.user.domain.repository;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(@Param("username") String username);

    Boolean existsByUsername(@Param("username") String username);
}
