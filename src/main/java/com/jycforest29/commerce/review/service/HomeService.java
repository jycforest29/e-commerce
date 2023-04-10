package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.controller.dto.ReviewResponseDto;

import java.util.List;

public interface HomeService {
    List<ReviewResponseDto> getHomeReviewList(String username);
}
