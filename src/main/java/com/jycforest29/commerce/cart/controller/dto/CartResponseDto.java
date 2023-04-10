package com.jycforest29.commerce.cart.controller.dto;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter // for 테스트
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartResponseDto {
    private List<CartUnitResponseDto> cartUnitResponseDtoList;
    private int totalPrice;

    public static CartResponseDto from(Cart cart){
        return CartResponseDto.builder()
                .cartUnitResponseDtoList(
                        cart.getCartUnitList().stream()
                                .map(s -> CartUnitResponseDto.from(s))
                                .collect(Collectors.toList())
                )
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}
