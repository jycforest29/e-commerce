package com.jycforest29.commerce.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.service.HomeService;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@Import(LoginAuthUserResolver.class)
@WebMvcTest
@ContextConfiguration(classes = HomeController.class)
class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HomeService homeService;
    private Item item;
    private Review review;
    private AuthUser authUser;
    private AuthUser otherUser;

//    @WithMockUser(username = "mock_user")
//    @Test
//    void 로그인_유저에_대해_홈화면의_내용이_정상_리턴된다() throws Exception {
//        // 커스텀 어노테이션의 정상 동작 확인
//        given(homeService.getHomeReviewList(anyString()).willReturn(Arrays.asList(ReviewResponseDto.from(review))));
//        mockMvc.perform(MockMvcRequestBuilders.get("/home")
//                        .param("username", authUser.getUsername()))
//                        .andDo(print())
//                        .andExpect(status().isOk());
//    }
}