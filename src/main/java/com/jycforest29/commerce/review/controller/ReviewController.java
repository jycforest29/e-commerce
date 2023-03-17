package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "/review")
@RequiredArgsConstructor
@RestController
public class ReviewController { // Long과 long의 차이 : Long은 클래스, long은 8bytes의 한계를 가진 primitive 자료형.
    private final ReviewService reviewService;

    @GetMapping("/{itemId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewListByItem(@PathVariable("itemId") Long itemId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewListByItem(itemId));
    }

    @GetMapping("/{itemId}/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewDetail(@PathVariable("itemId") Long itemId,
                                                             @PathVariable("reviewId") Long reviewId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewDetail(itemId, reviewId));
    }


    @PostMapping("/{itemId}")
    public ResponseEntity<Object> addReview(@PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody AddReviewRequestDto addReviewRequestDTO,
                                            @LoginAuthUser String username){
        reviewService.addReview(itemId, addReviewRequestDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{itemId}/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("itemId") Long itemId,
                                               @PathVariable("reviewId") Long reviewId,
                                               @Valid @RequestBody AddReviewRequestDto addReviewRequestDTO,
                                               @LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.updateReview(itemId, reviewId, addReviewRequestDTO, username));
    }

    @DeleteMapping("/{itemId}/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable("itemId") Long itemId,
                                               @PathVariable("reviewId") Long reviewId,
                                               @LoginAuthUser String username){
        reviewService.deleteReview(itemId, reviewId, username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/{itemId}/{reviewId}/like")
    public ResponseEntity<ReviewResponseDto> likeReview(@PathVariable("itemId") Long itemId,
                                             @PathVariable("reviewId") Long reviewId,
                                             @LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.likeReview(itemId, reviewId, username));
    }

    @DeleteMapping(value = "/{itemId}/{reviewId}/like")
    public ResponseEntity<ReviewResponseDto> removeLikeReview(@PathVariable("itemId") Long itemId,
                                                   @PathVariable("reviewId") Long reviewId,
                                                   @LoginAuthUser String username){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.removeLikeReview(itemId, reviewId, username));
    }
}
