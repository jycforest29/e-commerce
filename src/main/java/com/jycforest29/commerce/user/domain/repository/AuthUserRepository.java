package com.jycforest29.commerce.user.domain.repository;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    // 평범한 findBy문. 성능 최적화 할 것 안보임
    Optional<AuthUser> findByUsername(String username);

    // count와 달리 첫번째 결과만 조회하고 바로 return true를 하므로 exists의 성능이 더 좋음
    // 메서드 쿼리의 exists는 내부에서 limit으로 최적화 하고 있음 -> querydsl을 사용할 것이 아니면 따로 최적화 할 것 안보임
    Boolean existsByUsername(String username);
}
