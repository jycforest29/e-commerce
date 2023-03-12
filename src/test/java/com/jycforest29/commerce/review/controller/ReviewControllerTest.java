package com.jycforest29.commerce.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(LoginAuthUserResolver.class)
@WebMvcTest(ReviewController.class)
@ContextConfiguration(classes = ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;
    @Autowired
    private ObjectMapper objectMapper;
    @WithMockUser
    @Test
    void 특정_아이템에_대한_리뷰가_모두_리턴된다() throws Exception {
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
                .andExpect(jsonPath("$..title", "title").exists())
                .andExpect(jsonPath("$..contents", "contents").exists());
    }

    @WithMockUser
    @Test
    void 특정_아이템에_대한_특정_리뷰가_리턴된다() throws Exception {
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

    @WithMockUser(username = "testuser1")
    @Test
    void 특정_아이템에_대한_리뷰를_작성한다() throws Exception {
        // given
        AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
                .title("제목은 10~255 글자여야 합니다.")
                .contents("내용은 10~255 글자여야 합니다.")
                .build();
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(addReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/review/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "testuser1")
    @Test
    void 로그인한_유저가_작성한_리뷰를_수정한다() throws Exception {
        // given
        AddReviewRequestDto updateReviewRequestDto = AddReviewRequestDto.builder()
                .title("제목은 10~255 글자여야 합니다.")
                .contents("내용은 10~255 글자여야 합니다.")
                .build();
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "testuser1"
        );
        given(reviewService.updateReview(any(), any(), any(), any()))
                .willReturn(reviewResponseDto);
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(updateReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/review/{itemId}/{reviewId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", "제목은 10~255 글자여야 합니다.").exists())
                .andExpect(jsonPath("$.contents", "내용은 10~255 글자여야 합니다.").exists());
//        verify(reviewService).updateReview(1L, 1L, updateReviewRequestDto, "testuser1");
    }

    @WithMockUser(username = "testuser1")
    @Test
    void 로그인한_유저가_작성한_리뷰를_삭제한다() throws Exception {
        // given
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/{itemId}/{reviewId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testuser1")
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
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.title", "title").exists())
//                .andExpect(jsonPath("$.contents", "contents").exists());
        verify(reviewService).likeReview(1L, 1L, "testuser1");
    }
    @WithMockUser(username = "testuser1")
    @Test
    void 로그인한_유저가_특정_리뷰에_누른_좋아요를_취소한다() throws Exception {
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