package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.repository.OrderRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AuthUserRepository authUserRepository;
    private Item item;
    private AuthUser authUser;
    private AuthUser otherUser;
    private final int threadCnt = 2;

    @BeforeEach
    void init(){
        item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(100)
                        .build()
        );
        authUser = authUserRepository.save(
                AuthUser.builder()
                        .username("test_username")
                        .password("test_password")
                        .nickname("test_nickname_")
                        .build()
        );
        otherUser = authUserRepository.save(
                AuthUser.builder()
                        .username("test_username_other")
                        .password("test_password_other")
                        .nickname("test_nickname_other")
                        .build()
        );
    }

    @AfterEach
    void after(){
        itemRepository.deleteAll();
        authUserRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void 동시에_2명이_재고가_100개인_아이템을_주문한다() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        executorService.submit(() -> {
            try{
                orderService.makeOrder(item.getId(), authUser.getId(), Integer.valueOf(99));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        });
        executorService.submit(() -> {
            try{
                orderService.makeOrder(item.getId(), otherUser.getId(), Integer.valueOf(1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        //then
        assertThat(itemRepository.findById(item.getId()).get().getNumber()).isEqualTo(0);
    }

    @Test
    void 동시에_2명이_재고가_100개인_아이템을_1개씩_주문_취소한다(){
        assertThat(orderService.getOrderList(authUser).size()).isEqualTo(0);
    }

}