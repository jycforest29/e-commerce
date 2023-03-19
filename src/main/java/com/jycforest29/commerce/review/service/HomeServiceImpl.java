package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.proxy.AuthUserCacheProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeUnitRepository reviewLikeUnitRepository;
    private final AuthUserCacheProxy authUserCacheProxy;
    private final Clock clock;

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDto> getHomeReviewList(String username) {
        // 엔티티 가져옴
        AuthUser authUser = getAuthUser(username);
        List<AuthUser> likedAuthor = getLikedAuthor(authUser);

        return getAllHomeReviewList(likedAuthor)
                .stream()
                .map(s -> ReviewResponseDto.from(s))
                .collect(Collectors.toList());
    }

    private List<AuthUser> getLikedAuthor(AuthUser authUser){
        return reviewLikeUnitRepository.findAllByAuthUser(authUser)
                .stream()
                .map(s -> s.getReview().getAuthUser())
                .collect(Collectors.toList());
    }

    private List<Review> getAllHomeReviewList(List<AuthUser> likedReviewSet){
        LocalDateTime now = LocalDateTime.now(clock);
        List<Review> homeReviewList = likedReviewSet.stream()
                .distinct()
                .map(s -> reviewRepository.findAllByAuthUserIdAndCreatedWithin72Hours(s.getId(), now))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return homeReviewList;
    }
    private AuthUser getAuthUser(String username){
        return authUserCacheProxy.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }
}
