package com.jycforest29.commerce.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import com.jycforest29.commerce.review.controller.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.controller.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.ReviewService;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import com.jycforest29.commerce.utils.DockerComposeTestContainer;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
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
    ReviewResponseDto reviewResponseDto;
    @BeforeEach
    void init(){
        authUserRepository.save(AuthUser.builder()
                .username("testuser1")
                .password("pw1234@")
                .nickname("testuser")
                .build());
        reviewResponseDto = new ReviewResponseDto(
                "title",
                "contents",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "name",
                "testuser1"
        );
    }

    @AfterEach
    void after(){
        authUserRepository.deleteAll();
    }

    @Test
    void 특정_아이템에_대한_리뷰가_모두_리턴된다() throws Exception {
        // given
        given(reviewService.getReviewListByItem(1L)).willReturn(Arrays.asList(reviewResponseDto));
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/review/{itemId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].contents").value("contents"));
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 특정_아이템에_대한_특정_리뷰가_리턴된다() throws Exception {
        // given
        given(reviewService.getReviewDetail(1L, 1L)).willReturn(reviewResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/review/{itemId}/{reviewId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.contents").value("contents"));
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 특정_아이템에_대한_리뷰를_작성했지만_validation을_통과하지_못하고_status_code_400을_발생시킨다() throws Exception {
        // given
        AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
                .title("제목은")
                .contents("내용은")
                .build();
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(addReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/review/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저가_작성한_리뷰를_수정한다() throws Exception {
        // given
        AddReviewRequestDto updateReviewRequestDto = AddReviewRequestDto.builder()
                .title("제목은 10~255 글자여야 합니다.")
                .contents("내용은 10~255 글자여야 합니다.")
                .build();
        ReviewResponseDto updatedReviewResponseDto = new ReviewResponseDto(
                "제목은 10~255 글자여야 합니다.",
                "내용은 10~255 글자여야 합니다.",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "name",
                "testuser1"
        );
        given(reviewService.updateReview(1L, 1L, updateReviewRequestDto, "testuser1"))
                .willReturn(List.of(updatedReviewResponseDto));
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(updateReviewRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/review/{itemId}/{reviewId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목은 10~255 글자여야 합니다."))
                .andExpect(jsonPath("$[0].contents").value("내용은 10~255 글자여야 합니다."));
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저가_작성한_리뷰를_삭제한다() throws Exception {
        // given
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/{itemId}/{reviewId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저가_특정_리뷰에_좋아요를_누른다() throws Exception {
        // given
        given(reviewService.likeReview(1L, 1L, "testuser1"))
                .willReturn(List.of(reviewResponseDto));
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/{itemId}/{reviewId}/like", 1L, 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].contents").value("contents"));
    }
    @WithUserDetails(value = "testuser1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 로그인한_유저가_특정_리뷰에_누른_좋아요를_취소한다() throws Exception {
        // given
        given(reviewService.removeLikeReview(1L, 1L, "testuser1"))
                .willReturn(List.of(reviewResponseDto));
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/{itemId}/{reviewId}/like", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}