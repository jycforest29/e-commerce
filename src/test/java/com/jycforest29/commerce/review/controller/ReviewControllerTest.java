package com.jycforest29.commerce.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoginAuthUserResolver.class)
class ReviewControllerTest extends DockerComposeTestContainer {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;
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
                .build()
        );
    }

    @AfterEach
    void after(){
        authUserRepository.deleteAll();
    }

    @Test
    void ??????_????????????_??????_?????????_??????_????????????() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.getReviewListByItem(1L)).willReturn(Arrays.asList(reviewResponseDto));
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/review/{itemId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title", "title").exists())
                .andExpect(jsonPath("$.[0].contents", "contents").exists());
    }

    @Test
    void ??????_????????????_??????_??????_?????????_????????????() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.getReviewDetail(1L, 1L)).willReturn(reviewResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/review/{itemId}/{reviewId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", "title").exists())
                .andExpect(jsonPath("$.contents", "contents").exists());
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void ??????_????????????_??????_?????????_????????????() throws Exception {
        // given
        AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
                .title("????????? 10~255 ???????????? ?????????.")
                .contents("????????? 10~255 ???????????? ?????????.")
                .build();
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(addReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/review/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void ????????????_?????????_?????????_?????????_????????????() throws Exception {
        // given
        AddReviewRequestDto updateReviewRequestDto = AddReviewRequestDto.builder()
                .title("????????? 10~255 ???????????? ?????????.")
                .contents("????????? 10~255 ???????????? ?????????.")
                .build();
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "????????? 10~255 ???????????? ?????????.",
                "????????? 10~255 ???????????? ?????????.",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.updateReview(1L, 1L, updateReviewRequestDto, "testuser1"))
                .willReturn(reviewResponseDto);
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(updateReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/review/{itemId}/{reviewId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", "").exists())
                .andExpect(jsonPath("$.contents", "????????? 10~255 ???????????? ?????????.").exists());
//        verify(reviewService).updateReview(1L, 1L, updateReviewRequestDto, "testuser1");
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void ????????????_?????????_?????????_?????????_????????????() throws Exception {
        // given
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/{itemId}/{reviewId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void ????????????_?????????_??????_?????????_????????????_?????????() throws Exception {
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
    }
    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void ????????????_?????????_??????_?????????_??????_????????????_????????????() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.removeLikeReview(1L, 1L, "testuser1"))
                .willReturn(reviewResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/{itemId}/{reviewId}/like", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}