package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MadeOrderRepository extends JpaRepository<MadeOrder, Long> {
    // AuthUser:MadeOrder N:1
    // 다를 통해 일을 찾음
    List<MadeOrder> findAllByAuthUserOrderByCreatedAtDesc(@Param("authUser") AuthUser authUser);
}
