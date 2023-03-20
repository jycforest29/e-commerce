package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.order.proxy.async.OrderAsyncProxy;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    private final MadeOrderRepository madeOrderRepository;
    private final OrderUnitRepository orderUnitRepository;
    private final RedisLockRepository redisLockRepository;
    private final ItemRepository itemRepository;
    private final AuthUserRepository authUserRepository;
    private final OrderAsyncProxy orderAsyncProxy;

    @Transactional
    @Override
    public MadeOrderResponseDto makeOrder(Long itemId, int number, String username) throws InterruptedException {
        while(!redisLockRepository.lock(List.of(itemId))){
            Thread.sleep(100);
        }
        try{
            return makeOrderUnitAsync(itemId, number, username);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            redisLockRepository.unlock(List.of(itemId));
        }
    }

    // 장바구니에 있는 아이템을 전부 주문하기 위해선 어떻게 락을 걸어줘야 할까?
    // 일단 장바구니에 아이템 A, B가 있을때 아이템 A를 주문할 때 B가 품절되면 안됨
    // 즉 모든 아이템에 대해 수행 여부에 동일하게 보장되어야 함
    // -> 각 아이템이 속해있는 모든 테이블에 락을 걸어 한번에 처리해야
    @Transactional
    @Override
    public MadeOrderResponseDto makeOrderForCart(String username) throws InterruptedException, ExecutionException {
        // 엔티티 가져옴
        AuthUser authUser = getAuthUser(username);
        Cart cart = authUser.getCart();
        List<CartUnit> cartUnitList = cart.getCartUnitList();

        // 락을 걸어야 하는 아이템의 id 리스트 추출
        List<Long> itemIdListToLock = cartUnitList.stream()
                .map(s -> s.getItem().getId())
                .collect(Collectors.toList());

        while(!redisLockRepository.lock(itemIdListToLock)){
            Thread.sleep(100);
        }
        try{
            return null;
//            List<OrderUnit> orderUnitList = new ArrayList<>();
//            for(CartUnit cartUnit: cartUnitList){
//                orderUnitList.add(makeOrderUnitAsync(cartUnit.getItem().getId(), cartUnit.getNumber()));
//            }
//            MadeOrderResponseDto madeOrderResponseDto = madeOrderWithCommit(username, orderUnitList);
//            return madeOrderResponseDto;
        }finally {
            redisLockRepository.unlock(itemIdListToLock);
        }
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
    public void deleteOrder(Long madeOrderId, String username, List<Long> itemIdListToLock) throws InterruptedException{
//        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
//        MadeOrder madeOrder = getOrder(madeOrderId);
//        // 하나의 madeOrder는 아이템 페이지에서 바로 주문했느냐, 혹은 장바구니를 통해 주문했느냐에 따라 주문이 수행된 아이템의 개수가 다름
//        List<OrderUnit> orderUnitList = madeOrder.getOrderUnitList();
//        AuthUser authUser = getAuthUser(authUserId);
//        // 락을 걸어야 하는 아이템리스트 추출
//        List<Long> itemIdSetToLock = orderUnitList.stream()
//                .map(s -> s.getItem().getId())
//                .collect(Collectors.toList());
        while(!redisLockRepository.lock(itemIdListToLock)){
            Thread.sleep(100);
        }
        try{
            // 유효성 검증을 통해 검증 후, 엔티티 가져옴
            MadeOrder madeOrder = getMadeOrder(madeOrderId);
            List<OrderUnit> orderUnitList = madeOrder.getOrderUnitList();

            // 실제 item 개수 증가
            for(OrderUnit o : orderUnitList){
                deleteOrderUnitAsync(o.getItem().getId(), o.getNumber());
            }
            deleteOrderWithCommit(username, madeOrder, orderUnitList);
        }
        finally {
            redisLockRepository.unlock(itemIdListToLock);
        }
    }

    private MadeOrderResponseDto makeOrderUnitAsync(Long itemId, int number, String username)
            throws ExecutionException, InterruptedException {

        CompletableFuture<CompletableFuture<MadeOrderResponseDto>> orderUnitCompletableFuture =
                CompletableFuture.supplyAsync(() -> {
                    return orderAsyncProxy.makeOrderUnitAsync(itemId, number)
                    .thenApply(result -> {
                        return madeOrderWithCommit(username, Arrays.asList(result));
                    });
        });

        return orderUnitCompletableFuture.get().get();
    }

    private MadeOrderResponseDto madeOrderWithCommit(String username, List<OrderUnit> orderUnitList) {
        return orderAsyncProxy.madeOrderWithCommit(username, orderUnitList);
    }

    private void deleteOrderUnitAsync(Long itemId, int number) {
        orderAsyncProxy.deleteOrderUnitAsync(itemId, number);
    }

    private void deleteOrderWithCommit(String username, MadeOrder madeOrder, List<OrderUnit> orderUnitList) {
        orderAsyncProxy.deleteOrderWithCommit(username, madeOrder, orderUnitList);
    }


    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }

    private MadeOrder getMadeOrder(Long madeOrderId){
        return madeOrderRepository.findById(madeOrderId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }
}
