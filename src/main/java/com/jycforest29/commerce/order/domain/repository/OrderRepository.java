package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.MakeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<MakeOrder, Long> {
    List<MakeOrder> findAllByAuthUser(Long authUserId);
}
