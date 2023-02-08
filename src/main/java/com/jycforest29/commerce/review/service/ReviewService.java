package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
import com.jycforest29.commerce.review.domain.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getReviewListByItemId(Long itemId);

    void addReview(Long itemId, AddReviewRequestDTO addReviewRequestDTO, Long authUserId);

    Review getReview(Long reviewId);

    Review updateReview(Long itemId, Long authUserId, Long reviewId, AddReviewRequestDTO addReviewRequestDTO);

    void deleteReview(Long itemId, Long authUserId, Long reviewId);

    void likeReview(Long authUserId, Long reviewId);

    void removeLikeReview(Long authUserId, Long reviewId);
}
