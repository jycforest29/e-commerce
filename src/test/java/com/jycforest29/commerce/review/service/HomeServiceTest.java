package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewLikeUnitRepository reviewLikeUnitRepository;

    @InjectMocks
    private HomeServiceImpl homeService;

    private Item item;
    private AuthUser authUser;
    private AuthUser otherUser;
    private Review review;
    private ReviewLikeUnit reviewLikeUnit;

    @BeforeEach
    void init(){
        item = Item.builder()
                .name("test_item")
                .price(10000)
                .number(10)
                .build();
        item.setId(1L);

        authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        authUser.setId(1L);

        otherUser = AuthUser.builder()
                .username("other_username")
                .password("other_password")
                .nickname("other_nickname")
                .build();
        otherUser.setId(2L);

        review = Review.builder()
                .title("제목:제목은 10~255 글자여야 합니다.")
                .contents("내용:내용은 10~255 글자여야 합니다.")
                .item(item)
                .authUser(otherUser)
                .build();

        reviewLikeUnit = ReviewLikeUnit.builder()
                .authUser(authUser) // authUser가 otherUser가 작성한 Review에 대해 좋아요 누름.
                .review(review)
                .build();
    }
    @Test
    void 내가_좋아요를_눌렀던_리뷰의_작성자들이_50시간동안_작성한_리뷰를_가져온다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewLikeUnitRepository.findAllByAuthUserId(authUser.getId()))
                .willReturn(Arrays.asList(reviewLikeUnit));

    }
}