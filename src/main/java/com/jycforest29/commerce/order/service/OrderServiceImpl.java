package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.dto.OrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MakeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.OrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final AuthUserRepository authUserRepository;
    private final RedisLockRepository redisLockRepository;

    // !동시성 로직
    @Transactional
    @Override
    public OrderResponseDto makeOrder(Long itemId, Long authUserId, Integer number) {
        AuthUser authUser = getAuthUser(authUserId);
        Item item = getItem(itemId);
        if (item.getNumber() < number){
            throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
        }
        MakeOrder makeOrder = MakeOrder.builder()
                .authUser(authUser)
                .build();
        OrderUnit orderUnit = OrderUnit.builder()
                .item(item)
                .makeOrder(makeOrder)
                .number(number)
                .build();
        makeOrder.addOrderUnit(orderUnit);
        return OrderResponseDto.from(makeOrder);
    }

    // !동시성 로직
    @Override
    public OrderResponseDto makeOrderForCart(Long authUserId) {
        AuthUser authUser = getAuthUser(authUserId);
        Cart cart = authUser.getCart();
        List<CartUnit> cartUnitList = cart.getCartUnitList();
        for(CartUnit cartUnit : cartUnitList){
            if(cartUnit.getItem().getNumber() < cartUnit.getNumber()){
                throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
            }
        }

        MakeOrder makeOrder = MakeOrder.builder()
                .authUser(authUser)
                .build();
        makeOrder.addOrderUnitForCart(cartUnitList);
        return OrderResponseDto.from(makeOrder); // UPDATE
    }

    // 주문 전체 내역 보기
    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getOrderList(Long authUserId) {
        getAuthUser(authUserId);
        List<MakeOrder> makeOrderList = orderRepository.findAllByAuthUser(authUserId);
        List<OrderResponseDto> orderResponseDtoList = makeOrderList.stream()
                .map(s -> OrderResponseDto.from(s))
                .collect(Collectors.toList());
        return orderResponseDtoList;
    }

    // 주문 상세 보기
    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto getOrder(Long makeOrderId, Long authUserId) {
        getAuthUser(authUserId);
        return OrderResponseDto.from(getOrder(makeOrderId));
    }

    // !동시성 로직
    // Order 삭제시 연결된 OrderUnit 전부 삭제됨.
    @Transactional
    @Override
    public void deleteOrder(Long makeOrderId, Long authUserId) {
        getAuthUser(authUserId);
        getOrder(makeOrderId);
        orderRepository.deleteById(makeOrderId);
    }

    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        return authUser;
    }

    public Item getItem(Long itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return item;
    }

    public MakeOrder getOrder(Long orderId){
        MakeOrder makeOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return makeOrder;
    }
}
