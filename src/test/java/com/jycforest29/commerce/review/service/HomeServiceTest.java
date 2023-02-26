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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

//@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class HomeServiceTest {
    Logger logger = LoggerFactory.getLogger(HomeServiceTest.class);
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @SpyBean
    private Clock clock; // now() 는 static 이므로 @SpyBean 사용해야 함. 따라서 통합테스트로 변경.
    @Autowired
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

        // otherUser가 item에 대해 review 작성
        item.addReview(review);
        otherUser.addReview(review);
        // otherUser가 item에 대해 new_review 작성
        item.addReview(new_review);
        otherUser.addReview(new_review);
        // authUser가 review에 대해 좋아요 누름
        authUser.addReviewLikeUnit(reviewLikeUnit);
        review.addReviewLikeUnit(reviewLikeUnit);
    }

    // authUser는 otherUser가 방금 전 작성한 item에 대한 review에 좋아요를 눌렀다.(모킹이므로 쿼리 테스트는 불가)
    // 이후 otherUser는 item에 대해 추가로 new_review를 작성했다.
    @Test
    void 내가_좋아요를_눌렀던_리뷰의_작성자들을_가져온다(){
        //given
        given(reviewLikeUnitRepository.findAllByAuthUser(authUser))
                .willReturn(Arrays.asList(reviewLikeUnit)); // Set의 원소로 otherUser
        //when
        List<AuthUser> likedAuthUserSet = homeService.getLikedAuthor(authUser);
        //then
        assertThat(likedAuthUserSet).contains(otherUser);
    }

    @Test
    void 내가_좋아요를_눌렀던_리뷰의_작성자들이_50시간동안_작성한_리뷰를_가져온다(){
        //given-getAuthUser
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        //given-getLikedAuthor
        given(reviewLikeUnitRepository.findAllByAuthUser(authUser))
                .willReturn(Arrays.asList(reviewLikeUnit)); // 내가_좋아요를_눌렀던_리뷰의_작성자들을_가져온다 테스트 완료
        //given-getAllHomeReviewList
        //clock 테스트를 위해 특정 시간을 리턴하도록 고정
        LocalDateTime now = LocalDateTime.ofInstant(Instant.parse("2022-08-22T10:00:00Z"), ZoneId.systemDefault());
        given(clock.instant()).willReturn(Instant.parse("2022-08-22T10:00:00Z"));
        given(reviewRepository.findAllByAuthUserIdAndCreatedWithin50Hours(otherUserId, now))
                .willReturn(otherUser.getReviewList());
        //when
        List<ReviewResponseDto> result = homeService.getHomeReviewList(authUserId);
        //then
        assertThat(result.size()).isEqualTo(2); // review, new_review
    }
}