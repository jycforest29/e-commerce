package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartRepository;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private MadeOrderRepository madeOrderRepository;
    @Autowired
    private OrderUnitRepository orderUnitRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private RedisLockRepository redisLockRepository;
    @Autowired
    private CartUnitRepository cartUnitRepository;
    @Autowired
    private CartRepository cartRepository;
    private AuthUser authUser;
    private AuthUser otherUser;
    private final int threadCnt = 2;

    @BeforeEach
    void init(){
        authUser = authUserRepository.save(
                AuthUser.builder()
                        .username("test_username")
                        .password("test_password")
                        .nickname("test_nickname_")
                        .build()
        );
        otherUser = authUserRepository.save(
                AuthUser.builder()
                        .username("other_username")
                        .password("other_password")
                        .nickname("other_password")
                        .build()
        );
    }

    @AfterEach
    void after(){
        itemRepository.deleteAll();
        authUserRepository.deleteAll();
        madeOrderRepository.deleteAll();
        orderUnitRepository.deleteAll();
        cartRepository.deleteAll();
        cartUnitRepository.deleteAll();
    }

    @Nested
    class makeOrder{
        @Test
        void 동시에_2명이_재고가_100개인_아이템을_각각_99개와_1개_주문하여_모두_주문에_성공한다() throws InterruptedException {
            // 명시적으로 @Transactional을 해주지 않으면 @Test 내부에서는 transactional하게 동작하지 않음
            Item item = itemRepository.save(
                    Item.builder()
                            .name("test_item")
                            .price(10000)
                            .number(100)
                            .build()
            );

            ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
            CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

            executorService.submit(() -> {
                try{
                    orderService.makeOrder(item.getId(),99, authUser.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });

            executorService.submit(() -> {
                try{
                    orderService.makeOrder(item.getId(), 1, otherUser.getId());
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
        void 동시에_2명이_재고가_1개인_아이템을_1명은_장바구니_전체_주문하기로_주문하고_다른_1명은_직접_주문하여_둘중_한명은_주문을_실패한다()
                throws InterruptedException {
            Item item = itemRepository.save(
                    Item.builder()
                            .name("test_item")
                            .price(10000)
                            .number(1)
                            .build()
            );
            CartUnit cartUnit = CartUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            cartUnitRepository.save(cartUnit);

            ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
            CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

            executorService.submit(() -> {
                try{
                    orderService.makeOrderForCart(authUser.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });

            executorService.submit(() -> {
                try{
                    orderService.makeOrder(item.getId(), 1, otherUser.getId());
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
    }

    @Test
    void 동시에_2명이_재고가_0개인_아이템을_각각_99개와_1개씩_주문_취소한다() throws InterruptedException {
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(0)
                        .build()
        );

        orderService.makeOrder(item.getId(), 99, authUser.getId());
        orderService.makeOrder(item.getId(), 1, otherUser.getId());

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        executorService.submit(() -> {
            try{
                MadeOrder madeOrder = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(authUser).get(0);
                orderService.deleteOrder(madeOrder.getId(), authUser.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        });
        executorService.submit(() -> {
            try{
                MadeOrder madeOrder = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(otherUser).get(0);
                orderService.deleteOrder(madeOrder.getId(), otherUser.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        //then
        assertThat(itemRepository.findById(item.getId()).get().getNumber()).isEqualTo(100);
    }

    @Test
    void 동시에_2명이_재고가_0개인_아이템을_1명은_1개를_주문취소하고_다른_1명은_1개_주문하여_주문에_성공하면_성공여부는_그때그때_다르다()
            throws InterruptedException {
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(0)
                        .build()
        );
        orderService.makeOrder(item.getId(), 1, authUser.getId());

        // 고정된 스레드 풀이기에 순서가 보장되지 않음. 순서 보장을 위해선 newSingleThreadExecutor를 사용해 하나의 스레드만 생성해야 함.
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        executorService.submit(() -> {
            try{
                MadeOrder madeOrder = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(authUser).get(0);
                orderService.deleteOrder(madeOrder.getId(), authUser.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        });
        executorService.submit(() -> {
            try{
                orderService.makeOrder(item.getId(), 1, otherUser.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (CustomException e){
                System.out.println(e.getExceptionCode()+"발생");
            }
            finally {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        //then
        assertThat(itemRepository.findById(item.getId()).get().getNumber()).isLessThanOrEqualTo(1); // 항상 1이하
    }
}