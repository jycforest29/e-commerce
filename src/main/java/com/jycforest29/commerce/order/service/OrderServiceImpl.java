package com.jycforest29.commerce.order.service;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final MadeOrderRepository madeOrderRepository;
    private final OrderUnitRepository orderUnitRepository;
    private final ItemRepository itemRepository;
    private final AuthUserRepository authUserRepository;
    private final RedisLockRepository redisLockRepository;

    @Transactional
    @Override
    public MadeOrderResponseDto makeOrder(Long itemId, int number, Long authUserId) throws InterruptedException {
        // 아이템 한 종류에 대해서 주문하므로 itemId를 기준으로 락을 걸어줌
        while(!redisLockRepository.lock(List.of(itemId))){
            Thread.sleep(100);
        }
        try{
            // 엔티티 가져옴(유효성 검증은 컨트롤러에서 이미 완료함)
            AuthUser authUser = getAuthUser(authUserId);
            // Item 엔티티는 락이 걸려있는 상황에서 유효성 검증이 필요함
            Item item = getValidateItemByNumber(itemId, number);
            OrderUnit orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(number)
                    .build();

            // Cart와 다르게 연관 관계 편의 메서드를 static 으로 선언한 이유는 Cart는 AuthUser와 일대일 단방향 관계로,
            // AuthUser가 존재할 경우 항상 존재하게 짰기 때문에 Cart 객체를 생성해 줄 필요가 없는 반면 MakeOrder 객체는 새로 생성 필요
            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, Arrays.asList(orderUnit));
            madeOrderRepository.save(madeOrder);
            orderUnitRepository.save(orderUnit);

            // Cart와 다르게 단방향 관계인 item의 메서드를 호출하는 이유는 Cart는 item에 영향을 주지않는 반면, OrderUnit은 영향을 줌
            logger.info(String.valueOf(item.getNumber())+"에서");
            orderUnit.getItem().decreaseItemNumber(number);
            logger.info(number+"만큼 아이템의 수량 감소해서 현재 아이템의 수량은" +orderUnit.getItem().getNumber());
            itemRepository.save(item);

            // try에서 return 수행할 경우 finally 거쳐서 정상 종료됨.
            return MadeOrderResponseDto.from(madeOrder);
        }finally {
            redisLockRepository.unlock(List.of(itemId));
        }
    }

    // 장바구니에 있는 아이템을 전부 주문하기 위해선 어떻게 락을 걸어줘야 할까?
    // 일단 장바구니에 아이템 A, B가 있을때 아이템 A를 주문할 때 B가 품절되면 안됨
    // 즉 모든 아이템에 대해 수행 여부에 동일하게 보장되어야 함
    // 방법1. 각 아이템에 대해 락을 걸어 주문을 수행하다가 하나라도 실패하면 모두 반영하지 않음 -> 롤백 로직 따로 작성해
    // 방법2. 각 아이템이 속해있는 모든 테이블에 락을 걸어 한번에 처리함
    // -> setnx에 exec 사용해 방법2로 구현
    @Transactional
    @Override
    public MadeOrderResponseDto makeOrderForCart(Long authUserId) throws InterruptedException {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        AuthUser authUser = getAuthUser(authUserId);
        Cart cart = authUser.getCart();
        List<CartUnit> cartUnitList = cart.getCartUnitList();

        // List<CartUnit>를 List<OrderUnit>으로 변환
        List<OrderUnit> orderUnitList = cartUnitList.stream()
                .map(s -> OrderUnit.mapToOrderUnit(s))
                .collect(Collectors.toList());

        // 락을 걸어야 하는 아이템리스트 추출
        List<Long> itemIdSetToLock = orderUnitList.stream()
                .map(s -> s.getItem().getId())
                .collect(Collectors.toList());

        while(redisLockRepository.lock(itemIdSetToLock) != null){
            Thread.sleep(100);
        }
        try{
            logger.info(itemIdSetToLock.toString()+"에 대한 배치 락 구현");
            for(OrderUnit o : orderUnitList){
                getValidateItemByNumber(o.getItem().getId(), o.getNumber());
            }
            MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, orderUnitList);
            madeOrderRepository.save(madeOrder);
            orderUnitRepository.saveAll(orderUnitList);
            for(OrderUnit o : orderUnitList){
                logger.info(String.valueOf(o.getItem().getNumber())+"에서");
                o.getItem().decreaseItemNumber(o.getNumber());
                logger.info(o.getNumber()+"만큼 아이템의 수량 감소해서 현재 아이템의 수량은" +o.getItem().getNumber());
                itemRepository.save(o.getItem());
            }
            logger.info(String.valueOf("장바구니에 대한 전체 주문이 완료됨"));
            return MadeOrderResponseDto.from(madeOrder);
        }finally {
            redisLockRepository.unlock(itemIdSetToLock);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MadeOrderResponseDto> getOrderList(Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        AuthUser authUser = getAuthUser(authUserId);

        // findAllBy<Column name>을 사용할 경우 스프링 데이터 jpa에 의해 자동으로 쿼리문 생성됨
        List<MadeOrder> madeOrderList = madeOrderRepository.findAllByAuthUserOrderByCreatedAtDesc(authUser);
        List<MadeOrderResponseDto> madeOrderResponseDtoList = madeOrderList.stream()
                .map(s -> MadeOrderResponseDto.from(s))
                .collect(Collectors.toList());
        return madeOrderResponseDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public MadeOrderResponseDto getOrder(Long madeOrderId, Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        MadeOrder madeOrder = getOrder(madeOrderId);
        return MadeOrderResponseDto.from(madeOrder);
    }

    @Transactional
    @Override
    public void deleteOrder(Long madeOrderId, Long authUserId) throws InterruptedException {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        MadeOrder madeOrder = getOrder(madeOrderId);
        // 하나의 madeOrder는 아이템 페이지에서 바로 주문했느냐, 혹은 장바구니를 통해 주문했느냐에 따라 주문이 수행된 아이템의 개수가 다름
        List<OrderUnit> orderUnitList = madeOrder.getOrderUnitList();
        AuthUser authUser = getAuthUser(authUserId);
        // 락을 걸어야 하는 아이템리스트 추출
        List<Long> itemIdSetToLock = orderUnitList.stream()
                .map(s -> s.getItem().getId())
                .collect(Collectors.toList());

        while(redisLockRepository.lock(itemIdSetToLock) != null){
            Thread.sleep(100);
        }
        try{
            for(OrderUnit o : madeOrder.getOrderUnitList()){
                o.getItem().increaseItemNumber(o.getNumber());
                itemRepository.save(o.getItem());
            }
            // 연관관계 해제
            madeOrder.deleteOrder(authUser, orderUnitList);
            madeOrderRepository.deleteById(madeOrder.getId());
            orderUnitRepository.deleteAllByOrderUnitIdList(
                    orderUnitList.stream()
                            .map(s -> s.getId())
                            .collect(Collectors.toList())
            );

        }finally {
            redisLockRepository.unlock(itemIdSetToLock);
        }
    }

    @Transactional
    public Item getValidateItemByNumber(Long itemId, int number){
        Item item = getItem(itemId);
        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    // 현재 로컬 캐싱인데, AuthUser 객체에서는 madeOrderList에 접근이 가능함.
    // 따라서 메인 서버의 DB와 동기화 시차 때문에 주문 취소를
    // 했는데 반영되지 않은 상태로 캐시에 남아있는 경우가 있을 수 있음.
    // 이 경우 프로그램의 신뢰도가 떨어지므로 아예 캐싱을 사용하지 않음.
    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
        return authUser;
    }

    // 현재 로컬 캐싱이므로 재고와 직접적으로 관련있는 Item에는 캐싱 걸면 안됨
    public Item getItem(Long itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return item;
    }

    // 현재 로컬 캐싱이므로 재고와 직접적으로 관련있는 MadeOrder에는 캐싱 걸면 안됨
    public MadeOrder getOrder(Long madeOrderId){
        MadeOrder madeOrder = madeOrderRepository.findById(madeOrderId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return madeOrder;
    }
}
