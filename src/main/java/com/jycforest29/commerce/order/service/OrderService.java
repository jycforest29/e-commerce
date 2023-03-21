package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderService {
    MadeOrderResponseDto makeOrder(Long itemId, int number, String username) throws InterruptedException;
    MadeOrderResponseDto makeOrderForCart(String username) throws InterruptedException, ExecutionException;
    List<MadeOrderResponseDto> getOrderList(String username);
    MadeOrderResponseDto getOrder(Long itemId, String username);
    void deleteOrder(Long itemId, String username, List<Long> itemIdListToLock) throws InterruptedException;
}
