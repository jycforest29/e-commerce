package com.jycforest29.commerce.order.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/*
HttpStatus.OK 와 HttpStatus.ACCEPTED의 차이? OK는 처리가 완료되었음을 의미하고 ACCEPTED는 요청이 처리를 위해 수락되었지만 완료되지는
않았음을 의미
* */

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/{itemId}/order")
    public ResponseEntity<MadeOrderResponseDto> makeOrder(@PathVariable("item") Long itemId,
                                                          @RequestParam @Min(1) int number,
                                                          @LoginAuthUser Long authUserId) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrder(itemId, number, authUserId));
    }

    @PostMapping(value = "/cart/order")
    public ResponseEntity<MadeOrderResponseDto> makeOrderForCart(@LoginAuthUser Long authUserId)
            throws InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrderForCart(authUserId));
    }

    @GetMapping(value = "/order")
    public ResponseEntity<List<MadeOrderResponseDto>> getOrderList(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderList(authUserId));
    }

    @GetMapping(value = "/order/{orderId}")
    public ResponseEntity<MadeOrderResponseDto> getOrder(@PathVariable("orderId") Long orderId,
                                                         @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(orderId, authUserId));
    }

    @DeleteMapping(value = "/order/{orderId}")
    public ResponseEntity<Object> deleteOrder(@PathVariable("orderId") Long orderId,
                                              @LoginAuthUser Long authUserId) throws InterruptedException {
        orderService.deleteOrder(orderId, authUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
