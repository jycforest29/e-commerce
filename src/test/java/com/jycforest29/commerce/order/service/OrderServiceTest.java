package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.repository.OrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderUnitRepository orderUnitRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private AuthUserRepository authUserRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void 내가_주문을_수행한다(){

    }

    @Test
    void 내_장바구니_전체를_주문한다(){

    }

    @Test
    void 동시에_100명이_재고가_100개인_아이템을_주문한다(){

    }

    @Test
    void 내가_수행했던_주문을_취소한다(){

    }

    @Test
    void 동시에_100명이_재고가_100개인_아이템을_1개씩_주문_취소한다(){

    }

}