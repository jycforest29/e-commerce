package com.jycforest29.commerce.order.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.order.domain.dto.OrderResponseDto;
import com.jycforest29.commerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
HttpStatus.OK 와 HttpStatus.ACCEPTED의 차이? OK는 처리가 완료되었음을 의미하고 ACCEPTED는 요청이 처리를 위해 수락되었지만 완료되지는
않았음을 의미
* */

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    public ResponseEntity<OrderResponseDto> makeOrder(@PathVariable("item") Long itemId, @LoginAuthUser Long authUserId, Integer number){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrder(itemId, authUserId, number));
    }

    // 장바구니 그대로 주문
    public ResponseEntity<OrderResponseDto> makeOrderForCart(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrderForCart(authUserId));
    }

    public ResponseEntity<List<OrderResponseDto>> getOrderList(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderList(authUserId));
    }

    public ResponseEntity<OrderResponseDto> getOrder(Long orderId, @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(orderId, authUserId));
    }

    // 구매 취소
    public ResponseEntity<Object> deleteOrder(Long orderId, @LoginAuthUser Long authUserId){
        orderService.deleteOrder(orderId, authUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
