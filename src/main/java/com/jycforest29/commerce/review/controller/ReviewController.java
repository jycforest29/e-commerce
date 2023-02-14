package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("{itemId}/review")
    public ResponseEntity<List<Review>> getReviewListByItemId(@PathVariable("itemId") Long itemId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewListByItemId(itemId));
    }

    @PostMapping("{itemId}/review")
    public ResponseEntity<Object> addReview(@PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody AddReviewRequestDTO addReviewRequestDTO,
                                            @LoginAuthUser Long authUserId){
        reviewService.addReview(itemId, addReviewRequestDTO, authUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable("itemId") long itemId, @RequestParam("review") long reviewId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReview(reviewId));
    }

    @PutMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable("itemId") Long itemId, @LoginAuthUser Long authUserId, @PathVariable("review") Long reviewId, @RequestBody AddReviewRequestDTO addReviewRequestDTO){
//        reviewService.updateReview(itemId, authUserId, reviewId, addReviewRequestDTO);
//        return ResponseEntity.status(HttpStatus.OK).build();
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateReview(itemId, authUserId, reviewId, addReviewRequestDTO));
    }

    @DeleteMapping("{itemId}/review/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable("itemId") Long itemId, @LoginAuthUser Long authUserId, @PathVariable("reviewId") Long reviewId){
        reviewService.deleteReview(itemId, authUserId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/{itemId}/review/{reviewId}/like")
    public ResponseEntity<Object> likeReview(@PathVariable("itemId") Long itemId, @LoginAuthUser Long authUserId,@PathVariable("reviewId") Long reviewId){
        reviewService.likeReview(authUserId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{itemId}/review/{reviewId}/like")
    public ResponseEntity<Object> removeLikeReview(@PathVariable("itemId") Long itemId, @LoginAuthUser Long authUserId,@PathVariable("reviewId") Long reviewId){
        reviewService.removeLikeReview(authUserId, reviewId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
