//package com.jycforest29.commerce.common.aop;
//
//import com.jycforest29.commerce.common.exception.CustomException;
//import com.jycforest29.commerce.common.exception.ExceptionCode;
//import com.jycforest29.commerce.order.domain.entity.MadeOrder;
//import com.jycforest29.commerce.order.domain.entity.OrderUnit;
//import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.MethodParameter;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//public class DeleteItemIdListResolver implements HandlerMethodArgumentResolver {
//    private final MadeOrderRepository madeOrderRepository;
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return true;
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter,
//                                  ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest,
//                                  WebDataBinderFactory binderFactory) throws Exception {
//
//        Long madeOrderId;
//
//        MadeOrder madeOrder = madeOrderRepository.findById(madeOrderId)
//                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
//        // 하나의 madeOrder는 아이템 페이지에서 바로 주문했느냐, 혹은 장바구니를 통해 주문했느냐에 따라 주문이 수행된 아이템의 개수가 다름
//        List<OrderUnit> orderUnitList = madeOrder.getOrderUnitList();
//
//        // 락을 걸어야 하는 아이템리스트 추출
//        List<Long> itemIdListToLock = orderUnitList.stream()
//                .map(s -> s.getItem().getId())
//                .collect(Collectors.toList());
//        return itemIdListToLock;
//    }
//}
