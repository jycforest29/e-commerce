package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.review.domain.dto.HomeResponseDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {

    private final AuthUserRepository authUserRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;

    @Transactional(readOnly = true)
    @Override
    public List<HomeResponseDto> getHomeReviewList(@LoginAuthUser Long authUserId) {
        getAuthUser(authUserId);
        Set<AuthUser> authUserListOfLikedReview = getAuthUserListOfLikedReview(authUserId);
        return getAllReviewList(authUserListOfLikedReview)
                .stream()
                .map(s -> HomeResponseDto.from(s))
                .collect(Collectors.toList());
    }

    public Set<AuthUser> getAuthUserListOfLikedReview(Long authUserId){
        return reviewLikeUnitRepository.findAllByAuthUserId(authUserId)
                .stream().map(s -> s.getReview().getAuthUser())
                .collect(Collectors.toSet());
    }

    public List<Review> getAllReviewList(Set<AuthUser> authUserListOfLikedReview){
        List<Review> homeReviewList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(AuthUser authUserOfLikedReview : authUserListOfLikedReview){
            homeReviewList.addAll(
                    reviewRepository.findAllByAuthUserAndCreatedWithin50Hours(authUserOfLikedReview.getId(), now)
            );
        }
        return homeReviewList;
    }

    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        return authUser;
    }

}
