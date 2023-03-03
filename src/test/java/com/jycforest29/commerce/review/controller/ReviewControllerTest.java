//package com.jycforest29.commerce.review.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
//import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
//import com.jycforest29.commerce.review.service.ReviewService;
//import com.jycforest29.commerce.review.service.ReviewServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//@Import(LoginAuthUserResolver.class)
//@WebMvcTest(ReviewController.class)
//class ReviewControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ReviewService reviewService;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser(username = "test_user")
//    void 리뷰작성시_제목과_내용은_입력에_제한이_존재한다(){
//        AddReviewRequestDto addReviewRequestDTO = AddReviewRequestDto.builder()
//                .title("")
//                .contents("")
//                .build();
//
////        mockMvc.perform()
//    }
//
//}