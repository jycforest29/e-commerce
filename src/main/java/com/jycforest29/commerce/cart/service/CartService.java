package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.controller.dto.CartResponseDto;

public interface CartService {

    CartResponseDto addCartUnitToCart(Long itemId, int number, String username) throws InterruptedException;
    CartResponseDto getCartUnitList(String username);
    CartResponseDto deleteCartAll(String username);
    CartResponseDto deleteCartUnit(Long cartUnitId, String username) throws InterruptedException;
}
