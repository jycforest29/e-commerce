//package com.jycforest29.commerce.review.controller;
//
//import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
//import com.jycforest29.commerce.item.domain.entity.Item;
//import com.jycforest29.commerce.review.domain.entity.Review;
//import com.jycforest29.commerce.review.service.HomeServiceImpl;
//import com.jycforest29.commerce.security.config.JwtAuthenticationEntryPoint;
//import com.jycforest29.commerce.security.config.JwtRequestFilter;
//import com.jycforest29.commerce.security.service.JwtUserDetailsService;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.TestExecutionEvent;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Import({LoginAuthUserResolver.class})
//@SpringBootTest
//@AutoConfigureMockMvc
////@WebMvcTest(value = HomeController.class, includeFilters = @ComponentScan.Filter(classes= {EnableWebSecurity.class}))
//class HomeControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private AuthUserRepository authUserRepository;
//    @MockBean
//    private JwtRequestFilter jwtRequestFilter;
//    @MockBean
//    private JwtUserDetailsService jwtUserDetailsService;
//    @MockBean
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    @MockBean
//    private HomeServiceImpl homeService;
//    private Item item;
//    private Review review;
//    private AuthUser authUser;
//    private AuthUser otherUser;
//
//    @BeforeEach
//    void setUpEntity(){
//        authUser = authUserRepository.save(
//                AuthUser.builder()
//                        .username("mock_user")
//                        .password("mock_password")
//                        .nickname("mock_nickname")
//                        .build()
//        );
//        item = Item.builder()
//                .name("test_item")
//                .price(10000)
//                .number(10)
//                .build();
//        item.setId(1L);
//        otherUser = AuthUser.builder()
//                .username("other_username")
//                .password("other_password")
//                .nickname("other_nickname")
//                .build();
//        otherUser.setId(2L);
//        review = Review.builder()
//                .title("제목:제목은 10~255 글자여야 합니다.")
//                .contents("내용:내용은 10~255 글자여야 합니다.")
//                .item(item)
//                .authUser(otherUser)
//                .build();
//        item.addReview(review);
//        otherUser.addReview(review);
//    }
//
//    @AfterEach
//    void endUpForSecurity(){
//        authUserRepository.deleteAll();
//    }
//
//    @WithMockUser(username = "mock_user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    void 로그인_유저에_대해_홈화면의_내용이_정상_리턴된다() throws Exception {
//        List<HomeResponseDto> responseDto = Arrays.asList(HomeResponseDto.from(review));
//        // 커스텀 어노테이션의 정상 동작 확인
//        given(homeService.getHomeReviewList(authUser.getUsername())).willReturn(responseDto);
//        mockMvc.perform(MockMvcRequestBuilders.get("/home")
//                        .param("username", authUser.getUsername()))
//                        .andDo(print())
//                        .andExpect(status().isOk());
//    }
//}