package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @Mock
    private Clock clock;
    @InjectMocks
    private HomeServiceImpl homeService;
    private Item item;
    private AuthUser authUser;
    private Long authUserId = 1L;
    private AuthUser otherUser;
    private Long otherUserId = 2L;
    private Review review;
    private Review new_review;
    private ReviewLikeUnit reviewLikeUnit;

    @BeforeEach
    void init(){
        // 연관관계 생각하지 않은 가장 기본 엔티티 생성(실제 서비스에서의 모든 엔티티의 생성 방식)
        item = Item.builder()
                .name("test_item")
                .price(10000)
                .number(10)
                .build();

        authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();

        otherUser = AuthUser.builder()
                .username("other_username")
                .password("other_password")
                .nickname("other_nickname")
                .build();

        review = Review.builder()
                .title("제목:제목은 10~255 글자여야 합니다.")
                .contents("내용:내용은 10~255 글자여야 합니다.")
                .build();

        new_review = Review.builder()
                .title("제목:새롭게 작성된 리뷰의 제목은 10~255 글자여야 합니다.")
                .contents("내용:새롭게 작성된 리뷰의 내용은 10~255 글자여야 합니다.")
                .build();
        reviewLikeUnit = new ReviewLikeUnit();

        // 연관관계 매핑
        item.addReview(review);
        otherUser.addReview(review);
        item.addReview(new_review);
        otherUser.addReview(new_review);

        otherUser.addReviewLikeUnit(reviewLikeUnit);
        review.addReviewLikeUnit(reviewLikeUnit);
    }

    // authUser는 otherUser가 방금 전 작성한 item에 대한 review에 좋아요를 눌렀다.(모킹이므로 쿼리 테스트는 불가)
    // 이후 otherUser는 item에 대해 추가로 new_review를 작성했다.
    @Test
    void 내가_좋아요를_눌렀던_리뷰의_작성자들이_50시간동안_작성한_리뷰를_가져온다(){
        //given-getAuthUser
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        //given-getLikedAuthor
        given(reviewLikeUnitRepository.findAllByAuthUser(authUser))
                .willReturn(Arrays.asList(reviewLikeUnit)); // Set의 원소로 otherUser
        //given-getAllHomeReviewList
        //clock 테스트를 위해 특정 시간을 리턴하도록 고정
        given(clock.instant()).willReturn(Instant.parse("2022-08-10T00:00:00Z"));
        given(reviewRepository.findAllByAuthUserIdAndCreatedWithin50Hours(otherUserId,
                LocalDateTime.parse("2022-08-10T00:00:00Z")))
                .willReturn(otherUser.getReviewList());
        //when
        List<ReviewResponseDto> result = homeService.getHomeReviewList(authUserId);
        //then
        assertThat(result.size()).isEqualTo(2); // review, new_review
    }
}