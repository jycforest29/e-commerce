//package com.jycforest29.commerce.order.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
//import com.jycforest29.commerce.order.domain.dto.MadeOrderResponseDto;
//import com.jycforest29.commerce.order.service.OrderService;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.TestExecutionEvent;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.Arrays;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(LoginAuthUserResolver.class)
//class OrderControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private OrderService orderService;
//    @Autowired
//    private AuthUserRepository authUserRepository;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @BeforeEach
//    void init(){
//        authUserRepository.save(AuthUser.builder()
//                .username("testuser1")
//                .password("pw1234@")
//                .nickname("testuser")
//                .build());
//    }
//
//    @AfterEach
//    void after(){
//        authUserRepository.deleteAll();
//    }
//    MadeOrderResponseDto madeOrderResponseDto = new MadeOrderResponseDto();
//    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 한_아이템에_대한_주문을_수행한다() throws Exception {
//        // given
//        given(orderService.makeOrder(1L, 1, "testuser1")).willReturn(madeOrderResponseDto);
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.post("/{itemId}/order", 1L)
//                        .with(csrf()))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 로그인한_유저의_장바구니_전체에_대한_주문을_수행한다() throws Exception {
//        // given
//        given(orderService.makeOrderForCart("testuser1", Arrays.asList(1L))).willReturn(madeOrderResponseDto);
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/order")
//                        .with(csrf()))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 로그인한_유저의_주문내역이_모두_리턴된다() throws Exception {
//        // given
//        given(orderService.getOrderList("testuser1")).willReturn(Arrays.asList(madeOrderResponseDto));
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.get("/order")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//
//    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 한_주문내역의_상세정보가_리턴된다() throws Exception {
//        // given
//        given(orderService.getOrder(1L, "testuser1")).willReturn(madeOrderResponseDto);
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.get("/order/{madeOrderId}", 1L)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//
//    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 이전_주문을_취소한다() throws Exception {
//        // given
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.delete("/order/{madeOrderId}", 1L)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//}