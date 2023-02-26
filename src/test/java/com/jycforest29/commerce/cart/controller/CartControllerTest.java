package com.jycforest29.commerce.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.cart.service.CartServiceImpl;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@Import(LoginAuthUserResolver.class)
@WebMvcTest(CartControllerTest.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartServiceImpl cartService;

    @Autowired
    private ObjectMapper objectMapper;
}