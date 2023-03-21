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

    @Transactional(propagation = Propagation.NESTED)
    @Async("makeOrderUnitExecutor")
    public CompletableFuture<OrderUnit> makeOrderUnitAsync(Long itemId, int number){
        log.info("makeOrderUnitAsync() 호출됨 ");
        Item item = getValidateItemByNumber(itemId, number);

        OrderUnit orderUnit = OrderUnit.builder()
                .item(item)
                .number(number)
                .build();
        item.decreaseItemNumber(number); // dirty checking -> Transactional propagation 고려해야

        log.info("makeOrderUnitAsync() 종료됨 ");
        return CompletableFuture.completedFuture(orderUnit);
    }

    @Transactional(propagation = Propagation.NESTED)
    public MadeOrderResponseDto madeOrderWithCommit(String username, List<OrderUnit> orderUnitList){
        log.info("madeOrderWithCommit() 호출됨 ");
        AuthUser authUser = getAuthUser(username);
        MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, orderUnitList);
        madeOrderRepository.save(madeOrder);
        orderUnitRepository.saveAll(orderUnitList);

        log.info("madeOrderWithCommit() 종료됨 ");
        return MadeOrderResponseDto.from(madeOrder);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Async("deleteOrderUnitExecutor")
    public Boolean deleteOrderUnitAsync(Long itemId, int number){
        try{
            log.info("deleteOrderUnitAsync() 호출됨 ");
            Item item = getItem(itemId);
            item.increaseItemNumber(number);
            log.info("deleteOrderUnitAsync() 종료됨 "+item.getNumber());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void deleteOrderWithCommit(String username, MadeOrder madeOrder, List<OrderUnit> orderUnitList){
        log.info("deleteOrderWithCommit() 호출됨 ");
        AuthUser authUser = getAuthUser(username);
        List<Long> orderUnitIdListToDelete = madeOrder.deleteMadeOrder(authUser, orderUnitList);
        madeOrderRepository.deleteById(madeOrder.getId());
        orderUnitRepository.deleteAllByOrderUnitIdList(orderUnitIdListToDelete);
        log.info("deleteOrderWithCommit() 종료됨 ");
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
