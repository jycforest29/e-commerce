package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.proxy.async.OrderAsyncProxy;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    private final MadeOrderRepository madeOrderRepository;
    private final RedisLockRepository redisLockRepository;
    private final AuthUserRepository authUserRepository;
    private final OrderAsyncProxy orderAsyncProxy;

    @Transactional
    @Override
    public MadeOrderResponseDto makeOrder(Long itemId, int number, String username)
            throws InterruptedException, ExecutionException {

        HashMap<Long, Integer> map = new HashMap<>();
        map.put(itemId, number);
        while(!redisLockRepository.lock(List.of(itemId))){
            Thread.sleep(100);
        }
        try{
            OrderUnit orderUnit = orderAll(map).get(0);
            return orderAsyncProxy.madeOrderWithCommit(username, Arrays.asList(orderUnit));
        }finally {
            redisLockRepository.unlock(List.of(itemId));
        }
    }

    // 장바구니에 있는 아이템을 전부 주문하기 위해선 어떻게 락을 걸어줘야 할까?
    // 일단 장바구니에 아이템 A, B가 있을때 아이템 A를 주문할 때 B가 품절되면 안됨
    // 즉 모든 아이템에 대해 수행 여부에 동일하게 보장되어야 함
    // -> 각 아이템이 속해있는 모든 테이블에 락을 걸어 한번에 처리해야
    @Transactional
    @Override
    public MadeOrderResponseDto makeOrderForCart(String username)
            throws InterruptedException, ExecutionException {
        // 엔티티 가져옴
        AuthUser authUser = getAuthUser(username);
        Cart cart = authUser.getCart();
        List<CartUnit> cartUnitList = cart.getCartUnitList();

        // 락을 걸어야 하는 아이템의 id 리스트 추출
        List<Long> itemIdListToLock = cartUnitList.stream()
                .map(s -> s.getItem().getId())
                .collect(Collectors.toList());

        HashMap<Long, Integer> map = new HashMap<>();
        for (CartUnit cartUnit : cartUnitList){
            map.put(cartUnit.getItem().getId(), cartUnit.getNumber());
        }
        while(!redisLockRepository.lock(itemIdListToLock)){
            Thread.sleep(100);
        }
        try{
            List<OrderUnit> orderUnitList = orderAll(map);
            return orderAsyncProxy.madeOrderWithCommit(username, orderUnitList);
        }finally {
            redisLockRepository.unlock(itemIdListToLock);
        }
    }

    private List<OrderUnit> orderAll(HashMap<Long, Integer> pair){
//        ---------------------------------2 sec 487 ms----------------------------
//        List<OrderUnit> orderUnitList = pair.entrySet().stream()
//                .map(cartUnit -> orderAsyncProxy.makeOrderUnitAsync(cartUnit.getKey(), cartUnit.getValue()))
//                .collect(Collectors.toList());
//        return orderUnitList;

//        ---------------------------------2 sec 262 ms----------------------------
//        List<OrderUnit> orderUnitList = pair.entrySet().stream()
//                .map(cartUnit -> orderAsyncProxy.makeOrderUnitAsync(cartUnit.getKey(), cartUnit.getValue()))
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//        return orderUnitList;

//        ---------------------------------2 sec 212 ms----------------------------
        List<CompletableFuture<OrderUnit>> completableFutures = pair.entrySet().stream()
                .map(cartUnit -> orderAsyncProxy.makeOrderUnitAsync(cartUnit.getKey(), cartUnit.getValue()))
                .collect(Collectors.toList());

        CompletableFuture<List<OrderUnit>> allFutureResult = CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .thenApply(s -> completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        return allFutureResult.join();
    }

    @Transactional(readOnly = true)
    @Override
    public List<MadeOrderResponseDto> getOrderList(String username) {
        // 엔티티 가져옴
        AuthUser authUser = getAuthUser(username);
        List<MadeOrder> madeOrderList = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(authUser);

        return madeOrderList.stream()
                .map(s -> MadeOrderResponseDto.from(s))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public MadeOrderResponseDto getOrder(Long madeOrderId, String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        MadeOrder madeOrder = getMadeOrder(madeOrderId);
        return MadeOrderResponseDto.from(madeOrder);
    }

    @Transactional
    @Override
    public void deleteOrder(Long madeOrderId, String username) throws InterruptedException{
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        MadeOrder madeOrder = getMadeOrder(madeOrderId);
        // 하나의 madeOrder는 아이템 페이지에서 바로 주문했느냐, 혹은 장바구니를 통해 주문했느냐에 따라 주문이 수행된 아이템의 개수가 다름
        List<OrderUnit> orderUnitList = madeOrder.getOrderUnitList();
        // 락을 걸어야 하는 아이템리스트 추출
        List<Long> itemIdListToLock = orderUnitList.stream()
                .map(s -> s.getItem().getId())
                .collect(Collectors.toList());

        while(!redisLockRepository.lock(itemIdListToLock)){
            Thread.sleep(100);
        }
        try{
            for(OrderUnit orderUnit : orderUnitList){
                orderAsyncProxy.deleteOrderUnitAsync(orderUnit.getItem().getId(), orderUnit.getNumber());
            }
            orderAsyncProxy.deleteOrderWithCommit(username, madeOrder, orderUnitList);
        }
        finally {
            redisLockRepository.unlock(itemIdListToLock);
        }
    }

//    private void deleteOrderUnitAsync(String username, MadeOrder madeOrder, List<OrderUnit> orderUnitList) {
//        List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<>();
//        for(OrderUnit orderUnit : orderUnitList){
//            completableFutureList.add(CompletableFuture.supplyAsync(() -> {
//               return orderAsyncProxy.deleteOrderUnitAsync(orderUnit.getItem().getId(), orderUnit.getNumber());
//            }));
//        }
//
//        CompletableFuture
//                .allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]))
//                .thenApply(s -> completableFutureList.stream()
//                        .map(CompletableFuture::join)
//                        .collect(Collectors.toList()))
//                .thenAccept(s -> {
//                    if(!s.contains("false")){
//                        orderAsyncProxy.deleteOrderWithCommit(username, madeOrder, orderUnitList);
//                    }
//                });
//    }

    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }

    private MadeOrder getMadeOrder(Long madeOrderId){
        return madeOrderRepository.findById(madeOrderId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

}
