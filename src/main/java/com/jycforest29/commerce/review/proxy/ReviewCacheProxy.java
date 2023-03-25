package com.jycforest29.commerce.review.proxy;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewCacheProxy {
    private final ReviewRepository reviewRepository;

//    @Cacheable(value = "review", key = "#reviewId", cacheManager = "ehCacheManager")
//    public Optional<Review> findById(Long reviewId){
//        return reviewRepository.findById(reviewId);
//    }

    @Cacheable(value = "reviewListByItem", key = "#item.id", cacheManager = "ehCacheManager")
    public List<Review> findAllByItem(Item item){
        return reviewRepository.findAllByItem(item);
    }
}
