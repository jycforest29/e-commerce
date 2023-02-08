package com.jycforest29.commerce.cart.controller;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.service.CartService;
import com.jycforest29.commerce.common.aop.LoginAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // !number 양수 @Valid로 확인
    @PostMapping(value = "{itemId}/add")
    public ResponseEntity<CartResponseDto> addCartUnitToCart(@PathVariable("itemId") Long itemId, @RequestParam @Positive int number, @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.addCartUnitToCart(itemId, number, authUserId));
    }

    @GetMapping(value = "/cart")
    public ResponseEntity<CartResponseDto> getItemList(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartUnitList(authUserId));
    }

    // Cart 전체 초기화
    @DeleteMapping(value = "/cart")
    public ResponseEntity<CartResponseDto> deleteItemAll(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.deleteCartAll(authUserId));
    }

    // Cart에 CartUnit 삭제함.
    @DeleteMapping(value = "/cart/{cartUnitId}")
    public ResponseEntity<CartResponseDto> deleteItem(@PathVariable("cartUnitId") Long cartUnitId, @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.deleteCartUnit(cartUnitId, authUserId));
    }
}
