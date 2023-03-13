package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;

import java.util.List;

public interface OrderService {
    MadeOrderResponseDto makeOrder(Long itemId, int number, Long authUserId) throws InterruptedException;
    MadeOrderResponseDto makeOrderForCart(Long authUserId, List<Long> itemIdListLock) throws InterruptedException;
    List<MadeOrderResponseDto> getOrderList(Long authUserId);
    MadeOrderResponseDto getOrder(Long itemId, Long authUserId);
    void deleteOrder(Long itemId, Long authUserId, List<Long> itemIdListLock) throws InterruptedException;
}
