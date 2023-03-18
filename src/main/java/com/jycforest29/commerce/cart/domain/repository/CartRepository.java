package com.jycforest29.commerce.cart.domain.repository;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
