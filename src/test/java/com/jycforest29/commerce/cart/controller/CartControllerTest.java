package com.jycforest29.commerce.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.cart.service.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartControllerTest.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CartServiceImpl cartService;

    @Test
    void 나의_장바구니를_가져온다(){

    }
}