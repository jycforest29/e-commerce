package com.jycforest29.commerce.order.service.proxy;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.controller.dto.MadeOrderResponseDto;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCommitProxy {

    private final ItemRepository itemRepository;
    private final MadeOrderRepository madeOrderRepository;
    private final OrderUnitRepository orderUnitRepository;
    private final AuthUserRepository authUserRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MadeOrderResponseDto madeOrderWithCommit(String username, List<OrderUnit> orderUnitList){
        AuthUser authUser = getAuthUser(username);
        MadeOrder madeOrder = MadeOrder.addOrderUnit(authUser, orderUnitList);
        madeOrderRepository.save(madeOrder);
        orderUnitRepository.saveAll(orderUnitList);
        return MadeOrderResponseDto.from(madeOrder);
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
