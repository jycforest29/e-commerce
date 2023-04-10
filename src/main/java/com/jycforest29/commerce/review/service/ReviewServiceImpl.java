package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.review.controller.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.controller.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.service.proxy.ReviewCacheProxy;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;
    private final ItemRepository itemRepository;
    private final AuthUserRepository authUserRepository;
    private final ReviewCacheProxy reviewCacheProxy;

    @Cacheable(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDto> getReviewListByItem(Long itemId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Item item = getItem(itemId);

        // 리뷰 좋아요순으로 내림차순 정렬
        return reviewCacheProxy.findAllByItem(item)
                .stream()
                .sorted((a, b) -> a.getReviewLikeUnitList().size() > b.getReviewLikeUnitList().size() ? -1 : 1)
                .map(s -> ReviewResponseDto.from(s))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto getReviewDetail(Long itemId, Long reviewId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Item item = getItem(itemId);
        Review review = getReview(reviewId);
        return ReviewResponseDto.from(review);
    }

    @CachePut(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public List<ReviewResponseDto> addReview(Long itemId, AddReviewRequestDto addReviewRequestDTO, String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Item item = getItem(itemId);
        AuthUser authUser = getAuthUser(username);

        // 로그인한 유저가 해당 아이템을 구매했어야만 리뷰를 작성할 수 있음.
        // 유저의 주문 목록 가져오기
        List<OrderUnit> orderUnitList = authUser.getMadeOrderList().stream()
                        .flatMap(s -> s.getOrderUnitList().stream())
                        .collect(Collectors.toList());
        // 주문 목록 중 해당 아이템을 주문한 목록이 없을 경우 401 발생 => 추후 수정 필요
        orderUnitList.stream()
                .filter(s -> s.getItem().equals(item))
                .findFirst()
                .orElseThrow(() -> {throw new CustomException(ExceptionCode.UNAUTHORIZED);});

        // 리뷰 객체 생성
        Review review = Review.from(addReviewRequestDTO);

        // 다대일 양방향 연관관계 매핑
        item.addReview(review);
        authUser.addReview(review);

        // DB에 반영
        reviewRepository.save(review);

        return getReviewListByItem(itemId);
    }

    @CachePut(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public List<ReviewResponseDto> updateReview(Long itemId,
                                          Long reviewId,
                                          AddReviewRequestDto addReviewRequestDTO,
                                          String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Review review = getReview(reviewId);
        AuthUser authUser = getAuthUser(username);
        if(!review.getAuthUser().equals(authUser)){
            throw new CustomException(ExceptionCode.NOT_DONE_BY_AUTHUSER);
        }

        // dirty checking 통해 DB에 반영
        review.update(addReviewRequestDTO);
//        return ReviewResponseDto.from(review);
        return getReviewListByItem(itemId);
    }

    @CachePut(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public List<ReviewResponseDto> deleteReview(Long itemId, Long reviewId, String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Review review = getReview(reviewId);
        AuthUser authUser = getAuthUser(username);
        if(!review.getAuthUser().equals(authUser)){
            throw new CustomException(ExceptionCode.NOT_DONE_BY_AUTHUSER);
        }

        // 기존의 array 변환함
        reviewLikeUnitRepository.findAllByReview(review).stream()
                .forEach(s -> s.getAuthUser().deleteReviewLikeUnit(s));

        // 해당 리뷰에 속하는 모든 reviewLikeUnit도 연관관계 제거
        review.deleteAllReviewLikeUnit();

        // 다대일 양방향 연관관계 제거
        Item item = review.getItem();
        item.deleteReview(review);
        authUser.deleteReview(review);

        // DB에 반영
        reviewLikeUnitRepository.deleteAllByReviewId(review.getId());
        reviewRepository.deleteById(review.getId());

        return getReviewListByItem(itemId);
    }

    @CachePut(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public List<ReviewResponseDto> likeReview(Long itemId, Long reviewId, String username){
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Review review = getReview(reviewId);
        AuthUser authUser = getAuthUser(username);
        reviewLikeUnitRepository.findByReviewAndAuthUser(review, authUser)
                .ifPresent(s -> {throw new CustomException(ExceptionCode.REVIEW_LIKE_DUPLICATED);});
        if(review.getAuthUser().equals(authUser)){
            throw new CustomException(ExceptionCode.CANNOT_LIKE_DONE_BY_AUTHUSER);
        }

        // reviewLikeUnit 생성
        ReviewLikeUnit reviewLikeUnit = new ReviewLikeUnit();

        // 다대일 양방향 연관관계 매핑
        review.addReviewLikeUnit(reviewLikeUnit);
        authUser.addReviewLikeUnit(reviewLikeUnit);

        // DB에 반영
        reviewLikeUnitRepository.save(reviewLikeUnit);
//        return ReviewResponseDto.from(review);
        return getReviewListByItem(itemId);
    }

    @CachePut(value = "reviewListByItem", key = "#itemId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public List<ReviewResponseDto> removeLikeReview(Long itemId, Long reviewId, String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Review review = getReview(reviewId);
        AuthUser authUser = getAuthUser(username);
        // likeReview과 달리 reviewLikeUnit 가져옴
        // 하나의 리뷰에 대해 한명의 유저는 딱 한번만 좋아요를 할 수 있음
        ReviewLikeUnit reviewLikeUnit = reviewLikeUnitRepository.findByReviewAndAuthUser(review, authUser)
                .orElseThrow(() -> {throw new CustomException(ExceptionCode.REVIEW_LIKE_NOT_EXISTS);});

        // 다대일 양방향 연관관계 제거
        authUser.deleteReviewLikeUnit(reviewLikeUnit);
        review.deleteReviewLikeUnit(reviewLikeUnit);

        // DB에 반영
        // delete와 deleteById를 비교했을때, 성능 차이는 없어보임
            // deleteById는 findById + delete
            // deleteById는 findById 조회 시 데이터가 없을 경우 고정으로 발생하는 예외가 존재해 이 부분 커스텀 불가
        reviewLikeUnitRepository.deleteById(reviewLikeUnit.getId());
//        return ReviewResponseDto.from(review);
        return getReviewListByItem(itemId);
    }

    private Item getItem(Long itemId){
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }

    private Review getReview(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }
}
