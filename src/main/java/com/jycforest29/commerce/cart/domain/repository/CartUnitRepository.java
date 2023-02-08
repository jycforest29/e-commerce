package com.jycforest29.commerce.cart.domain.repository;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartUnitRepository extends JpaRepository<CartUnit, Long> {
}
