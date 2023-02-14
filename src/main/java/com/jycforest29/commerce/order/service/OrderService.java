package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.order.domain.dto.OrderResponseDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;

import java.util.List;

public interface OrderService {
    OrderResponseDto makeOrder(Long itemId, Long authUserId, Integer number) throws InterruptedException;
    OrderResponseDto makeOrderForCart(Long authUserId);
    List<OrderResponseDto> getOrderList(AuthUser authUser);
    OrderResponseDto getOrder(Long itemId, Long authUserId);
    void deleteOrder(Long itemId, Long authUserId) throws InterruptedException;
}
