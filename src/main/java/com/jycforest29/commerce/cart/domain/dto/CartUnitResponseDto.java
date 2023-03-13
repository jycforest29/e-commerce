package com.jycforest29.commerce.cart.domain.dto;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CartUnitResponseDto {
    private String name;
    private int orderPrice;
    private int number;
    private Boolean available;

    public static CartUnitResponseDto from(CartUnit cartUnit){
        return CartUnitResponseDto.builder()
                .name(cartUnit.getItem().getName())
                .orderPrice(cartUnit.getItem().getPrice() )
                .number(cartUnit.getNumber())
                .available(cartUnit.getAvailable())
                .build();
    }
}
