package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.order.domain.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto makeOrder(Long itemId, Long authUserId, Integer number);
    OrderResponseDto makeOrderForCart(Long authUserId);
    List<OrderResponseDto> getOrderList(Long authUserId);
    OrderResponseDto getOrder(Long itemId, Long authUserId);
    void deleteOrder(Long itemId, Long authUserId);
}
