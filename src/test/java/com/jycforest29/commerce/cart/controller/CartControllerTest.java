package com.jycforest29.commerce.cart.controller;

import com.jycforest29.commerce.cart.service.CartService;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(LoginAuthUserResolver.class)
@WebMvcTest
@ContextConfiguration(classes = CartController.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc; // 테스트를 위해 브라우저나 WAS의 동작을 똑같이 처리해 줄 수 있는 환경
    @MockBean
    private CartService cartService; // @WebMvcTest는 서비스, 리포지토리 관련 빈은 스캔하지 않기에 MockBean 사용

    @Test
    @WithMockUser(username = "test_user")
    void 로그인한_유저의_장바구니_목록을_가져온다() throws Exception {
        given(cartService.getCartUnitList(anyString())).willReturn(null);
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk());
    }
}