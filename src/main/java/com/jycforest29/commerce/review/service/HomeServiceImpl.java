package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {
    private final AuthUserRepository authUserRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDto> getHomeReviewList(Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        AuthUser authUser = getAuthUser(authUserId);
        Set<AuthUser> likedAuthor = getLikedAuthor(authUser);

        return getAllHomeReviewList(likedAuthor)
                .stream()
                .map(s -> ReviewResponseDto.from(s))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Set<AuthUser> getLikedAuthor(AuthUser authUser){
        return reviewLikeUnitRepository.findAllByAuthUser(authUser)
                .stream()
                .map(s -> s.getReview().getAuthUser())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Review> getAllHomeReviewList(Set<AuthUser> likedReviewSet){
        LocalDateTime now = LocalDateTime.now(clock);
        List<Review> homeReviewList = likedReviewSet.stream()
                .map(s -> reviewRepository.findAllByAuthUserIdAndCreatedWithin50Hours(s.getId(), now))
                .flatMap(s_ -> s_.stream())
                .collect(Collectors.toList());
        return homeReviewList;
    }

    @Cacheable(value = "authuser", key = "#authUserId", unless="#result == null", cacheManager = "ehCacheManager")
    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
        return authUser;
    }
}
