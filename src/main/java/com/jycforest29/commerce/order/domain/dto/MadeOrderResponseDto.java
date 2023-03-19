package com.jycforest29.commerce.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MadeOrderResponseDto {
    private String username;
    private List<OrderUnitResponseDto> orderUnitResponseDtoList;
    private int totalPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static MadeOrderResponseDto from(MadeOrder madeOrder){
        return MadeOrderResponseDto.builder()
                .username(madeOrder.getAuthUser().getUsername())
                .orderUnitResponseDtoList(madeOrder.getOrderUnitList().stream()
                        .map(s -> OrderUnitResponseDto.from(s))
                        .collect(Collectors.toList()))
                .totalPrice(madeOrder.getTotalPrice())
                .createdAt(madeOrder.getCreatedAt())
                .build();
    }
}
