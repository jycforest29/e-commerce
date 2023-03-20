package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.item.proxy.ItemCacheProxy;
import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import com.jycforest29.commerce.user.proxy.AuthUserCacheProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
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

    @Transactional
    @Override
    public MadeOrderResponseDto makeOrder(Long itemId, int number, String username) throws InterruptedException {
        while(!redisLockRepository.lock(List.of(itemId))){
            Thread.sleep(100);
        }
        try{
            // Item 엔티티는 락이 걸려있는 상황에서 유효성 검증이 필요함
            Item item = getValidateItemByNumber(itemId, number);

            // 엔티티 가져옴
            AuthUser authUser = getAuthUser(username);
            OrderUnit orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(number)
                    .build();

            // Cart와 다르게 연관 관계 편의 메서드를 static 으로 선언한 이유는 Cart는 AuthUser와 일대일 단방향 관계로,
            // AuthUser가 존재할 경우 항상 존재하게 짰기 때문에 Cart 객체를 생성해 줄 필요가 없는 반면 MakeOrder 객체는 새로 생성 필요
            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, Arrays.asList(orderUnit));
            madeOrderRepository.save(madeOrder);
            orderUnitRepository.save(orderUnit);

            // 실제 item 개수 감소(item은 영속성 컨텍스트에 존재하므로 dirty checking 수행됨)
            item.decreaseItemNumber(number);
            return MadeOrderResponseDto.from(madeOrder);
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
    public MadeOrderResponseDto makeOrderForCart(String username, List<Long> itemIdListToLock)
            throws InterruptedException {
//        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
//        AuthUser authUser = getAuthUser(authUserId);
//        Cart cart = authUser.getCart();
//        List<CartUnit> cartUnitList = cart.getCartUnitList();
//
//        // List<CartUnit>를 List<OrderUnit>으로 변환
//        List<OrderUnit> orderUnitList = cartUnitList.stream()
//                .map(s -> OrderUnit.mapToOrderUnit(s))
//                .collect(Collectors.toList());
//
//        // 락을 걸어야 하는 아이템리스트 추출
//        List<Long> itemIdListToLock = orderUnitList.stream()
//                .map(s -> s.getItem().getId())
//                .collect(Collectors.toList());

        while(!redisLockRepository.lock(itemIdListToLock)){
            Thread.sleep(100);
        }
        try{
            // 엔티티 가져옴
            AuthUser authUser = getAuthUser(username);
            Cart cart = authUser.getCart();
            List<CartUnit> cartUnitList = cart.getCartUnitList();
            List<OrderUnit> orderUnitList = cartUnitList.stream()
                .map(s -> OrderUnit.mapToOrderUnit(s))
                .collect(Collectors.toList());

            // Item 엔티티는 락이 걸려있는 상황에서 유효성 검증이 필요함
            for(OrderUnit o : orderUnitList){
                getValidateItemByNumber(o.getItem().getId(), o.getNumber());
            }

            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, orderUnitList);
            madeOrderRepository.save(madeOrder);
            orderUnitRepository.saveAll(orderUnitList);
            for(OrderUnit o : orderUnitList){
                Item item = o.getItem();
                // 실제 item 개수 감소(item은 영속성 컨텍스트에 존재하므로 dirty checking 수행됨)
                item.decreaseItemNumber(o.getNumber());
            }
            return MadeOrderResponseDto.from(madeOrder);
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
    public void deleteOrder(Long madeOrderId, String username, List<Long> itemIdListToLock) throws InterruptedException {
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
            AuthUser authUser = getAuthUser(username);
            for(OrderUnit o : madeOrder.getOrderUnitList()){
                Item item = getItem(o.getItem().getId());
                // 실제 item 개수 증가(item은 영속성 컨텍스트에 존재하므로 dirty checking 수행됨)
                item.increaseItemNumber(o.getNumber());
            }
            try{
                List<Long> orderUnitIdListToDelete = madeOrder.deleteMadeOrder(authUser, orderUnitList);
                madeOrderRepository.deleteById(madeOrder.getId());
                orderUnitRepository.deleteAllByOrderUnitIdList(orderUnitIdListToDelete);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        finally {
            redisLockRepository.unlock(itemIdListToLock);
        }
    }
    
    private Item getValidateItemByNumber(Long itemId, int number){
        Item item = getItem(itemId);
        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }

    private Item getItem(Long itemId){
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

    private MadeOrder getMadeOrder(Long madeOrderId){
        return madeOrderRepository.findById(madeOrderId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }
}
