package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;

public interface CartService {

    CartResponseDto addCartUnitToCart(Long itemId, int number, String username) throws InterruptedException;
    CartResponseDto getCartUnitList(String username);
    CartResponseDto deleteCartAll(Long authUserId);
    CartResponseDto deleteCartUnit(Long cartUnitId, Long authUserId) throws InterruptedException;
}
