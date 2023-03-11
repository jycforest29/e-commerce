//package com.jycforest29.commerce.review.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
//import com.jycforest29.commerce.review.domain.entity.Review;
//import com.jycforest29.commerce.review.dto.ReviewResponseDto;
//import com.jycforest29.commerce.review.service.HomeService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Import(LoginAuthUserResolver.class)
//@WebMvcTest
//@ContextConfiguration(classes = HomeController.class)
//class HomeControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private HomeService homeService;
//    private ReviewResponseDto reviewResponseDto;
//    @WithMockUser(username = "test_user")
//    @Test
//    void 로그인_유저에_대해_홈화면의_내용이_정상_리턴된다() throws Exception {
//        reviewResponseDto = ReviewResponseDto.builder()
//                .title("test_title")
//                .contents("test_contents")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .username("test_user")
//                .itemId(1L)
//                .build();
//
//        // 커스텀 어노테이션의 정상 동작 확인
//        given(homeService.getHomeReviewList(anyString())).willReturn(Arrays.asList(reviewResponseDto));
//        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
////                        .andDo(print())
//                        .andExpect(status().isOk());
//    }
//
//}