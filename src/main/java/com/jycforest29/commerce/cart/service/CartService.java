package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;

public interface CartService {

    CartResponseDto addCartUnitToCart(Long itemId, Integer number, Long authUserId) throws InterruptedException;
    CartResponseDto getCartUnitList(Long authUserId);
    CartResponseDto deleteCartAll(Long authUserId);
    CartResponseDto deleteCartUnit(Long cartUnitId, Long authUserId);
}
