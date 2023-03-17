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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {
    private final AuthUserRepository authUserRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDto> getHomeReviewList(String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        AuthUser authUser = getAuthUser(username);
        List<AuthUser> likedAuthor = getLikedAuthor(authUser);

        return getAllHomeReviewList(likedAuthor)
                .stream()
                .map(s -> ReviewResponseDto.from(s))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuthUser> getLikedAuthor(AuthUser authUser){
        return reviewLikeUnitRepository.findAllByAuthUser(authUser)
                .stream()
                .map(s -> s.getReview().getAuthUser())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Review> getAllHomeReviewList(List<AuthUser> likedReviewSet){
        LocalDateTime now = LocalDateTime.now(clock);

        List<Review> homeReviewList = likedReviewSet.stream()
                .distinct()
                .map(s -> reviewRepository.findAllByAuthUserIdAndCreatedWithin72Hours(2L, now))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return homeReviewList;
    }

    @Cacheable(value = "authUser", key = "#username",  cacheManager = "ehCacheManager")
    public AuthUser getAuthUser(String username){
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
        return authUser;
    }

}
