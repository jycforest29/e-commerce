package com.jycforest29.commerce.order.controller.dto;

import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderUnitResponseDto {
    private String name;
    private int orderPrice;
    private int number;

    public static OrderUnitResponseDto from(OrderUnit orderUnit){
        return OrderUnitResponseDto.builder()
                .name(orderUnit.getItem().getName())
                .orderPrice(orderUnit.getItem().getPrice() )
                .number(orderUnit.getNumber())
                .build();
    }
}
