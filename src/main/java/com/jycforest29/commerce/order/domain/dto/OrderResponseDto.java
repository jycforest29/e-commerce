package com.jycforest29.commerce.order.domain.dto;

import com.jycforest29.commerce.order.domain.entity.MakeOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// 빌더의 작동 방식은 생성자가 있을 경우 => 별도의 생성자 생성 x, 생성자가 없을 경우 => 모든 멤버 변수를 파라미터로 받는 기본 생성자 생성
public class OrderResponseDto {
//    private Member member;
//    private List<Image> imageList;
//    private LocalDateTime doneAt;
//    private Integer totalPrice;
//
//    public static OrderDto from(Order order){
//        return OrderDto.builder()
//                .member(order.getMember())
//                .imageList(order.getImageList())
//                .doneAt(order.getDoneAt())
//                .totalPrice(order.getTotalPrice())
//                .build();
//    }
    private MakeOrder makeOrder;

    public static OrderResponseDto from(MakeOrder makeOrder){
        return OrderResponseDto.builder()
                .makeOrder(makeOrder)
                .build();
    }
}
