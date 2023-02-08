package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService{
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;
    private final AuthUserRepository authUserRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Review> getReviewListByItemId(Long itemId) {
        return reviewRepository.findAllByItemId(getItem(itemId).getId())
                .stream()
                .sorted(Comparator.comparing(s -> s.getReviewLikeUnitList().size()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addReview(Long itemId, AddReviewRequestDTO addReviewRequestDTO, Long authUserId) {
        Item item = getItem(itemId);
        AuthUser authUser = getAuthUser(authUserId);
        Review review = Review.of(addReviewRequestDTO, item, authUser);
        itemRepository.save(item.addReview(review));
        authUserRepository.save(authUser.addReview(review));
    }

    @Transactional
    @Override
    public Review updateReview(Long itemId, Long authUserId, Long reviewId, AddReviewRequestDTO addReviewRequestDTO) {
        Item item = getItem(itemId);
        AuthUser authUser = getAuthUser(authUserId);
        Review review = getReview(reviewId);
        if(!review.getAuthUser().getId().equals(authUserId)){
            throw new CustomException(ExceptionCode.NOT_DONE_BY_AUTHUSER);
        }
        review.update(addReviewRequestDTO);
        return review;
    }

    @Transactional
    @Override
    public void deleteReview(Long itemId, Long authUserId, Long reviewId) {
        Item item = getItem(itemId);
        AuthUser authUser = getAuthUser(authUserId);
        Review review = getReview(reviewId);
        if(review.getAuthUser().getId().equals(authUserId)){
            item.deleteReview(review);
            authUser.deleteReview(review);
        }else{
            throw new CustomException(ExceptionCode.NOT_DONE_BY_AUTHUSER);
        }
    }

    @Transactional
    public void likeReview(Long authUserId, Long reviewId){
        AuthUser authUser = getAuthUser(authUserId);
        Review review = getReview(reviewId);
        reviewLikeUnitRepository.findByAuthUserIdAndReviewId(authUserId, reviewId)
                        .ifPresent(s -> {throw new CustomException(ExceptionCode.REVIEW_LIKE_DUPLICATED);});

        ReviewLikeUnit reviewLikeUnit = ReviewLikeUnit.builder()
                .review(review)
                .authUser(authUser)
                .build();
        authUser.addReviewLikeUnit(reviewLikeUnit);
        review.addReviewLikeUnit(reviewLikeUnit);
    }

    @Transactional
    @Override
    public void removeLikeReview(Long authUserId, Long reviewId) {
        AuthUser authUser = getAuthUser(authUserId);
        Review review = getReview(reviewId);

        reviewLikeUnitRepository.findByAuthUserIdAndReviewId(authUserId, reviewId)
                .ifPresent(s -> {
                    authUser.deleteReviewLikeUnit(s);
                    review.deleteReviewLikeUnit(s);
                });
    }

    public Item getItem(Long itemId){
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

    public AuthUser getAuthUser(Long authUserId){
        return authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    public Review getReview(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

}
