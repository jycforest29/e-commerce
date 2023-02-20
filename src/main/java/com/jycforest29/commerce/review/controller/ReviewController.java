package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController { // Long과 long의 차이 : Long은 클래스, long은 8bytes의 한계를 가진 primitive 자료형.
    private final ReviewService reviewService;

    @GetMapping("{itemId}/review")
    public ResponseEntity<List<ReviewResponseDto>> getReviewListByItem(@PathVariable("itemId") Long itemId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewListByItem(itemId));
    }

    @PostMapping("{itemId}/review")
    public ResponseEntity<Object> addReview(@PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody AddReviewRequestDto addReviewRequestDTO,
                                            @LoginAuthUser Long authUserId){
        reviewService.addReview(itemId, addReviewRequestDTO, authUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewDetail(@PathVariable("itemId") Long itemId,
                                            @PathVariable("reviewId") Long reviewId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewDetail(itemId, reviewId));
    }

    @PutMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("itemId") Long itemId,
                                               @PathVariable("reviewId") Long reviewId,
                                               @Valid @RequestBody AddReviewRequestDto addReviewRequestDTO,
                                               @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.updateReview(itemId, reviewId, addReviewRequestDTO, authUserId));
    }

    @DeleteMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable("itemId") Long itemId,
                                               @PathVariable("reviewId") Long reviewId,
                                               @LoginAuthUser Long authUserId){
        reviewService.deleteReview(itemId, reviewId, authUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "{itemId}/review/{reviewId}/like")
    public ResponseEntity<ReviewResponseDto> likeReview(@PathVariable("itemId") Long itemId,
                                             @PathVariable("reviewId") Long reviewId,
                                             @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.likeReview(itemId, reviewId, authUserId));
    }

    @DeleteMapping(value = "{itemId}/review/{reviewId}/like")
    public ResponseEntity<ReviewResponseDto> removeLikeReview(@PathVariable("itemId") Long itemId,
                                                   @PathVariable("reviewId") Long reviewId,
                                                   @LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.removeLikeReview(itemId, reviewId, authUserId));
    }
}
