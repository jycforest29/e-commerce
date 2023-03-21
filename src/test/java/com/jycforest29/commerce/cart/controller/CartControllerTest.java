package com.jycforest29.commerce.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.dto.CartUnitResponseDto;
import com.jycforest29.commerce.cart.service.CartService;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoginAuthUserResolver.class)
class CartControllerTest extends DockerComposeTestContainer {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CartService cartService;
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void init(){
        authUserRepository.save(AuthUser.builder()
                .username("testuser1")
                .password("pw1234@")
                .nickname("testuser")
                .build());
    }

    @AfterEach
    void after(){
        authUserRepository.deleteAll();
    }

    CartUnitResponseDto cartUnitResponseDto = new CartUnitResponseDto(
            "name",
            1000,
            1,
            true
    );
    CartResponseDto cartResponseDto = new CartResponseDto(
            Arrays.asList(cartUnitResponseDto),
            1000
    );

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 카트에_아이템을_1개_담는다() throws Exception {
        // given
        given(cartService.addCartUnitToCart(1L, 1, "testuser1")).willReturn(cartResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/{itemId}/add", 1L)
                        .param("number", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$..name").value("name"))
                .andExpect(jsonPath("$.totalPrice").value(1000));
    }
    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저의_장바구니를_가져온다() throws Exception {
        // given
        given(cartService.getCartUnitList("testuser1")).willReturn(cartResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name").value("name"))
                .andExpect(jsonPath("$.totalPrice").value(1000));
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저의_장바구니를_삭제한다() throws Exception {
        // given
        CartResponseDto afterDeleted = new CartResponseDto(
                new ArrayList<>(),
                0
        );
        given(cartService.deleteCartAll("testuser1")).willReturn(afterDeleted);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(0));
    }
    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 카트에서_특정_아이템을_삭제한다() throws Exception {
        // given
        given(cartService.deleteCartUnit(1L, "testuser1")).willReturn(cartResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{cartUnitId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name").value("name"))
                .andExpect(jsonPath("$.totalPrice").value(1000));
    }
}