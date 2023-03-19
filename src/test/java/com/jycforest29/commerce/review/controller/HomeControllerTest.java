package com.jycforest29.commerce.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.HomeService;
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

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoginAuthUserResolver.class)
class HomeControllerTest extends DockerComposeTestContainer {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HomeService homeService;
    @Autowired
    private AuthUserRepository authUserRepository;

    @BeforeEach
    void init(){
        authUserRepository.save(AuthUser.builder()
                .username("testuser1")
                .password("pw1234@")
                .nickname("testuser")
                .build()
        );
    }

    @AfterEach
    void after(){
        authUserRepository.deleteAll();
    }

    private ReviewResponseDto reviewResponseDto;
    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인_유저에_대해_홈화면의_내용이_정상_리턴된다() throws Exception {
        reviewResponseDto = ReviewResponseDto.builder()
                .title("title")
                .contents("contents")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("testuser1")
                .name("name")
                .build();

        // 커스텀 어노테이션의 정상 동작 확인
        given(homeService.getHomeReviewList("testuser1")).willReturn(Arrays.asList(reviewResponseDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].title").value("title"))
                        .andExpect(jsonPath("$[0].contents").value("contents"));
    }
}