package com.jycforest29.commerce.common.aop;

import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
import com.jycforest29.commerce.order.domain.repository.OrderUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class CartItemIdListResolver{
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
}
