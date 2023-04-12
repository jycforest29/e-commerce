package com.jycforest29.commerce.review.service.proxy;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.controller.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCacheProxy {
    private final ReviewRepository reviewRepository;

//    @Cacheable(value = "review", key = "#reviewId", cacheManager = "ehCacheManager")
//    public Optional<Review> findById(Long reviewId){
//        return reviewRepository.findById(reviewId);
//    }

    @Cacheable(value = "reviewListByItem", key = "#item.id", cacheManager = "ehCacheManager")
    public List<ReviewResponseDto> findAllByItem(Item item){
        return reviewRepository.findAllByItem(item)
                .stream()
                .sorted((a, b) -> a.getReviewLikeUnitList().size() > b.getReviewLikeUnitList().size() ? -1 : 1)
                .map(s -> ReviewResponseDto.from(s))
                .collect(Collectors.toList());
    }
}
