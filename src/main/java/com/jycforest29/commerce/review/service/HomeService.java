package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.review.domain.dto.HomeResponseDto;

import java.util.List;

public interface HomeService {
    List<HomeResponseDto> getHomeReviewList(Long authUserId);
}
