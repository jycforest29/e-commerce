package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.controller.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.controller.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.domain.entity.Review;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDto> getReviewListByItem(Long itemId);

    ReviewResponseDto getReviewDetail(Long itemId, Long reviewId);

    List<ReviewResponseDto> addReview(Long itemId, AddReviewRequestDto addReviewRequestDTO, String username);

    List<ReviewResponseDto> updateReview(Long itemId,
                                   Long reviewId,
                                   AddReviewRequestDto addReviewRequestDTO,
                                   String username);

    List<ReviewResponseDto> deleteReview(Long itemId, Long reviewId, String username);

    List<ReviewResponseDto> likeReview(Long itemId, Long reviewId, String username);

    List<ReviewResponseDto> removeLikeReview(Long itemId, Long reviewId, String username);
}
