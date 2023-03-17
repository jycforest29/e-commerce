package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDto> getReviewListByItem(Long itemId);

    ReviewResponseDto getReviewDetail(Long itemId, Long reviewId);

    void addReview(Long itemId, AddReviewRequestDto addReviewRequestDTO, String username);

    ReviewResponseDto updateReview(Long itemId,
                                   Long reviewId,
                                   AddReviewRequestDto addReviewRequestDTO,
                                   String username);

    void deleteReview(Long itemId, Long reviewId, String username);

    ReviewResponseDto likeReview(Long itemId, Long reviewId, String username);

    ReviewResponseDto removeLikeReview(Long itemId, Long reviewId, String username);
}
