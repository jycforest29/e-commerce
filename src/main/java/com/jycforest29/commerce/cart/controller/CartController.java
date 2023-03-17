package com.jycforest29.commerce.cart.controller;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.service.CartService;
import com.jycforest29.commerce.common.aop.LoginAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RequiredArgsConstructor
@RestController
public class CartController {
    private final CartService cartService;

    // @RequestParam의 기본은 required = true
    @PostMapping(value = "{itemId}/add")
    public ResponseEntity<CartResponseDto> addCartUnitToCart(@PathVariable("itemId") Long itemId,
                                                             @RequestParam @Min(1) int number,
                                                             @LoginAuthUser String username)
            throws InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.addCartUnitToCart(itemId, number, username));
    }

    @GetMapping(value = "/cart")
    public ResponseEntity<CartResponseDto> getCartUnitList(@LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartUnitList(username));
    }

    @DeleteMapping(value = "/cart")
    public ResponseEntity<CartResponseDto> deleteCartAll(@LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.deleteCartAll(username));
    }

    @DeleteMapping(value = "/cart/{cartUnitId}")
    public ResponseEntity<CartResponseDto> deleteCartUnit(@PathVariable("cartUnitId") Long cartUnitId,
                                                          @LoginAuthUser String username) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.deleteCartUnit(cartUnitId, username));
    }
}
