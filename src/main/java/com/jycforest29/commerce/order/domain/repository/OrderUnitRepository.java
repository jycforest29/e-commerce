package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderUnitRepository extends JpaRepository<OrderUnit, Long> {
}
