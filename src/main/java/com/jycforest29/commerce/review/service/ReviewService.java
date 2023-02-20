package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDto> getReviewListByItem(Long itemId);

    ReviewResponseDto getReviewDetail(Long itemId, Long reviewId);

    void addReview(Long itemId, AddReviewRequestDto addReviewRequestDTO, Long authUserId);

    ReviewResponseDto updateReview(Long itemId,
                        Long reviewId,
                        AddReviewRequestDto addReviewRequestDTO,
                        Long authUserId);

    void deleteReview(Long itemId, Long reviewId, Long authUserId);

    ReviewResponseDto likeReview(Long itemId, Long reviewId, Long authUserId);

    ReviewResponseDto removeLikeReview(Long itemId, Long reviewId, Long authUserId);
}
