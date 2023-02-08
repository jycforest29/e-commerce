//package com.jycforest29.commerce.review.controller;
//
//import com.jycforest29.commerce.review.service.HomeService;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(HomeControllerTest.class)
//class HomeControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private HomeService homeService;
//
//    @Test
//    @WithMockUser(username = "test_user")
//    @DisplayName("로그인_유저_home_내용_정상_리턴")
//    void getHomeReviewList() throws Exception {
//        when(homeService.getHomeReviewList(Long.valueOf("test_user"))).thenReturn(null);
//        mockMvc.perform(get("/home").param("authUserId", "test_user"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//}