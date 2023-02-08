//package com.jycforest29.commerce.review.controller;
//
//import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
//import com.jycforest29.commerce.review.service.ReviewService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@WebMvcTest(ReviewController.class)
//class ReviewControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ReviewService reviewService;
//
//    @Test
//    void 리뷰작성시_제목과_내용은_입력에_제한이_존재한다(){
//        AddReviewRequestDTO addReviewRequestDTO = AddReviewRequestDTO.builder()
//                .title("")
//                .contents("")
//                .build();
//
////        mockMvc.perform()
//    }
//
//}