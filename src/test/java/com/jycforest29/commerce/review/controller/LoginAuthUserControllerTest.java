package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.ReviewService;
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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginAuthUserControllerTest extends DockerComposeTestContainer {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthUserRepository authUserRepository;
    @MockBean
    private ReviewService reviewService;
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

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저가_특정_리뷰에_좋아요를_누른다() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.likeReview(1L, 1L, "testuser1"))
                .willReturn(reviewResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/{itemId}/{reviewId}/like", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", "title").exists())
                .andExpect(jsonPath("$.contents", "contents").exists());
        verify(reviewService).likeReview(1L, 1L, "testuser1");
    }
}
