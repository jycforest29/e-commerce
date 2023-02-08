package com.jycforest29.commerce.cart.domain.dto;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.item.domain.entity.Item;
import lombok.*;

import java.util.HashMap;
import java.util.List;

// !from 메서드 부분 싱글톤으로 구현하기.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자의 생성 방지
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private List<CartUnit> cartUnit;
    private Integer totalPrice;

    public static CartResponseDto from(Cart cart){
        return CartResponseDto.builder()
                .cartUnit(cart.getCartUnitList())
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}
