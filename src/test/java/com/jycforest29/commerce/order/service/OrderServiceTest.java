package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartRepository;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest extends DockerComposeTestContainer{
    private static final int threadCnt = 2;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private RedisLockRepository redisLockRepository;
    @Autowired
    private MadeOrderRepository madeOrderRepository;
    @Autowired
    private OrderUnitRepository orderUnitRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private CartUnitRepository cartUnitRepository;
    @Autowired
    private CartRepository cartRepository;
    private AuthUser authUser;
    private AuthUser otherUser;

    @BeforeEach
    void beforeEach(){
        authUser = authUserRepository.save(
                AuthUser.builder()
                        .username("test_username")
                        .password("test_password")
                        .nickname("test_nickname")
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
    void afterEach(){
        orderUnitRepository.deleteAll();
        cartUnitRepository.deleteAll();
        madeOrderRepository.deleteAll();
        authUserRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Nested
    class MakeOrderConcurrently {
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(100)
                        .build()
        );

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        @RepeatedTest(10)
        void ?????????_2??????_?????????_100??????_????????????_??????_99??????_1???_????????????_??????_?????????_????????????() throws InterruptedException {
            // ??????????????? @Transactional??? ????????? ????????? @Test ??????????????? transactional?????? ???????????? ??????
            executorService.submit(() -> {
                try {
                    orderService.makeOrder(item.getId(), 99, authUser.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
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
    @Nested
    class RunMakeOrderAndMarkCartConcurrently{
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(100)
                        .build()
        );
        Item otherItem = itemRepository.save(
                Item.builder()
                        .name("test_item_other")
                        .price(10000)
                        .number(1)
                        .build()
        );

        CartUnit cartUnit = CartUnit.builder()
                .item(item)
                .number(99)
                .build();

        CartUnit cartUnitOther = CartUnit.builder()
                .item(otherItem)
                .number(1)
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        @RepeatedTest(10)
        void ?????????_2??????_?????????_100??????_????????????_1??????_????????????_???????????????_99???_????????????_??????_1??????_??????_1???_?????????_??????_?????????_????????????()
                throws InterruptedException {
            // authUser??? ??????????????? item ?????? 1??? ??????
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            authUser.getCart().addCartUnitToCart(cartUnitOther, otherItem.getPrice());
            cartUnitRepository.save(cartUnit);
            cartUnitRepository.save(cartUnitOther);

            executorService.submit(() -> {
                try{
                    orderService.makeOrderForCart(authUser.getId(), Arrays.asList(item.getId(), otherItem.getId()));
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
            assertThat(itemRepository.findById(otherItem.getId()).get().getNumber()).isEqualTo(0);
        }

    }

    @Nested
    class DeleteOrderConcurrently{
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(0)
                        .build()
        );
        Long authUserMadeOrderId;
        Long otherUserMadeOrderId;

        void authUser???_item_99???_????????????(){
            // authUser??? item 99???, otherUser??? item 1??? ?????????.
            OrderUnit orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(99)
                    .build();
            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, Arrays.asList(orderUnit));
            authUserMadeOrderId = madeOrderRepository.save(madeOrder).getId();
            orderUnitRepository.save(orderUnit);
        }

        void otherUser???_item_1???_????????????(){
            OrderUnit orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            MadeOrder madeOrder = MadeOrder.addOrderUnit(otherUser, Arrays.asList(orderUnit));
            otherUserMadeOrderId = madeOrderRepository.save(madeOrder).getId();
            orderUnitRepository.save(orderUnit);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        @RepeatedTest(10)
        void ?????????_2??????_?????????_0??????_????????????_??????_99??????_1??????_??????_????????????() throws InterruptedException {
            authUser???_item_99???_????????????();
            otherUser???_item_1???_????????????();

            executorService.submit(() -> {
                try{
                    orderService.deleteOrder(authUserMadeOrderId, authUser.getId(), Arrays.asList(item.getId()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
            executorService.submit(() -> {
                try{
                    orderService.deleteOrder(otherUserMadeOrderId, otherUser.getId(), Arrays.asList(item.getId()));
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
    }

    @Nested
    class MakeOrderAndDeleteOrderConcurrently{
        Item item = itemRepository.save(
                Item.builder()
                        .name("test_item")
                        .price(10000)
                        .number(0)
                        .build()
        );

        void authUser???_item_1???_????????????(){
            OrderUnit orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, Arrays.asList(orderUnit));
            madeOrderRepository.save(madeOrder);
            orderUnitRepository.save(orderUnit);
        }

        @RepeatedTest(10)
        void ?????????_2??????_?????????_0??????_????????????_1??????_1??????_??????????????????_??????_1??????_1???_????????????_??????_?????????_1????????????()
                throws InterruptedException {
            authUser???_item_1???_????????????();

            // ????????? ????????? ???????????? ????????? ???????????? ??????. ?????? ????????? ????????? newSingleThreadExecutor??? ????????? ????????? ???????????? ???????????? ???.
            ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
            CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

            executorService.submit(() -> {
                try{
                    MadeOrder madeOrder = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(authUser).get(0);
                    orderService.deleteOrder(madeOrder.getId(), authUser.getId(), Arrays.asList(item.getId()));
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
                    System.out.println(e.getExceptionCode()+"??????");
                }
                finally {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            //then
            assertThat(itemRepository.findById(item.getId()).get().getNumber()).isLessThanOrEqualTo(1); // <= 1
        }
    }
}