package com.jycforest29.commerce.order.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(LoginAuthUserResolver.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc; // 테스트를 위해 브라우저나 WAS의 동작을 똑같이 처리해 줄 수 있는 환경
    @MockBean
    private OrderService orderService; // @WebMvcTest는 서비스, 리포지토리 관련 빈은 스캔하지 않기에 MockBean 사용


}