package com.jycforest29.commerce.order.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.order.controller.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
HttpStatus.OK 와 HttpStatus.ACCEPTED의 차이? OK는 처리가 완료되었음을 의미하고 ACCEPTED는 요청이 처리를 위해 수락되었지만 완료되지는
않았음을 의미
* */

@Validated
@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/{itemId}/order")
    public ResponseEntity<MadeOrderResponseDto> makeOrder(@PathVariable("itemId") Long itemId,
                                                          @RequestParam @Min(1) int number,
                                                          @LoginAuthUser String username)
            throws InterruptedException, ExecutionException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrder(itemId, number, username));
    }

    @PostMapping(value = "/cart/order")
    public ResponseEntity<MadeOrderResponseDto> makeOrderForCart(@LoginAuthUser String username)
            throws InterruptedException, ExecutionException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.makeOrderForCart(username));
    }

    @GetMapping(value = "/order")
    public ResponseEntity<List<MadeOrderResponseDto>> getOrderList(@LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderList(username));
    }

    @GetMapping(value = "/order/{madeOrderId}")
    public ResponseEntity<MadeOrderResponseDto> getOrder(@PathVariable("madeOrderId") Long madeOrderId,
                                                         @LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(madeOrderId, username));
    }

    @DeleteMapping(value = "/order/{madeOrderId}")
    public ResponseEntity<Object> deleteOrder(@PathVariable("madeOrderId") Long madeOrderId,
                                              @LoginAuthUser String username) throws InterruptedException {
        orderService.deleteOrder(madeOrderId, username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
