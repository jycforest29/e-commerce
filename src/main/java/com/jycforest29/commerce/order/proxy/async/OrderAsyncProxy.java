package com.jycforest29.commerce.order.proxy.async;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAsyncProxy {
    private final ItemRepository itemRepository;
    private final MadeOrderRepository madeOrderRepository;
    private final OrderUnitRepository orderUnitRepository;
    private final AuthUserRepository authUserRepository;

    // 왜 장바구니 로직의 비동기 처리가 어려운지?
    // @Transactional(propagation = Propagation.REQUIRES_NEW) 까지 같이 수행되어야 하는데,
    // 함수 호출 시간과 스레드 수행 시간의 차이 때문인지 자꾸 블로킹이 안됨.
    // 그렇다면 모든 OrderUnit에 대해 makeOrderUnitAsync()은 병렬적으로 실행되고 그 결과가 나올때까지 블로킹 되어야 함.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async("makeOrderUnitExecutor")
    public CompletableFuture<OrderUnit> makeOrderUnitAsync(Long itemId, int number){
        Item item = getValidateItemByNumber(itemId, number);
        OrderUnit orderUnit = OrderUnit.builder()
                .item(item)
                .number(number)
                .build();
        item.decreaseItemNumber(number); // dirty checking -> Transactional propagation 고려해야
        return CompletableFuture.completedFuture(orderUnit);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MadeOrderResponseDto madeOrderWithCommit(String username, List<OrderUnit> orderUnitList){
        AuthUser authUser = getAuthUser(username);
        MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, orderUnitList);
        madeOrderRepository.save(madeOrder);
        orderUnitRepository.saveAll(orderUnitList);
        return MadeOrderResponseDto.from(madeOrder);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async("deleteOrderUnitExecutor")
    public void deleteOrderUnitAsync(Long itemId, int number){
        Item item = getItem(itemId);
        item.increaseItemNumber(number);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteOrderWithCommit(String username, MadeOrder madeOrder, List<OrderUnit> orderUnitList){
        AuthUser authUser = getAuthUser(username);
        List<Long> orderUnitIdListToDelete = madeOrder.deleteMadeOrder(authUser, orderUnitList);
        orderUnitRepository.deleteAllByOrderUnitIdList(orderUnitIdListToDelete);
        madeOrderRepository.deleteById(madeOrder.getId());
    }

    private Item getValidateItemByNumber(Long itemId, int number){
        Item item = getItem(itemId);
        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    private Item getItem(Long itemId){
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }
}
