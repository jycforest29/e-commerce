package com.jycforest29.commerce.cart.domain.dto;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartUnitResponseDto {
    private String name;
    private int orderPrice;
    private int number;
    private boolean available;

    public static CartUnitResponseDto from(CartUnit cartUnit){
        return CartUnitResponseDto.builder()
                .name(cartUnit.getItem().getName())
                .orderPrice(cartUnit.getItem().getPrice() )
                .number(cartUnit.getNumber())
                .available(cartUnit.getAvailable())
                .build();
    }
}
