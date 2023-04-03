package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.proxy.ReviewCacheProxy;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ReviewCacheProxy reviewCacheProxy;
    @Mock
    private AuthUserRepository authUserRepository;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
            .title("제목:제목은 10~255 글자여야 합니다.")
            .contents("내용:내용은 10~255 글자여야 합니다.")
            .build();
    Item item = Item.builder()
            .name("test_item")
            .price(10000)
            .number(10)
            .build();
    Long itemId = 1L;
    AuthUser authUser = AuthUser.builder()
            .username("test_username")
            .password("test_password")
            .nickname("test_nickname")
            .build();
    AuthUser otherUser = AuthUser.builder()
            .username("test_username_other")
            .password("test_password_other")
            .nickname("test_nickname_other")
            .build();

    @Nested
    class ReadReview{
        Review review;
        Long reviewId;
        Review moreLikedReview;
        Long moreLikedReviewId;
        ReviewLikeUnit reviewLikeUnit;

        @BeforeEach
        void init(){
            // item에 대해 authUser가 review를 작성, otherUser가 moreLikedReview를 작성
            // moreLikedReview에 대해 authUser가 좋아요를 누름
            review = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            reviewId = 1L;
            moreLikedReview = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            moreLikedReviewId = 2L;
            reviewLikeUnit = new ReviewLikeUnit();

            authUser.addReview(review);
            otherUser.addReview(moreLikedReview);
            item.addReview(review);
            item.addReview(moreLikedReview);
            moreLikedReview.addReviewLikeUnit(reviewLikeUnit);
            authUser.addReviewLikeUnit(reviewLikeUnit);
        }

        @Test
        void 클릭한_아이템의_모든_리뷰가_리뷰의_좋아요개수_기준으로_내림차순_정렬되어_리턴된다(){
            // given
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
            given(reviewRepository.findAllByItem(item)).willReturn(Arrays.asList(review, moreLikedReview));
            //when
            List<ReviewResponseDto> result = reviewService.getReviewListByItem(itemId);
            //then
            assertThat(result.get(0).getUsername()).isEqualTo(otherUser.getUsername());
            assertThat(result.get(1).getUsername()).isEqualTo(authUser.getUsername());
        }
    }

    @Nested
    class CreateReview{
        MadeOrder madeOrder;
        OrderUnit orderUnit;
        @BeforeEach
        void init(){
            orderUnit = OrderUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            madeOrder = MadeOrder.addOrderUnit(authUser, List.of(orderUnit));
        }
        @Test
        void 내가_구매한_아이템에_대해_리뷰를_작성한다(){
            // given
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            // when
            reviewService.addReview(itemId, addReviewRequestDto, authUser.getUsername());
            //then
            assertThat(item.getReviewList().size()).isEqualTo(1);
            assertThat(authUser.getReviewList().size()).isEqualTo(1);
        }

        @Test
        void 내가_구매하지_않은_아이템에_대해_리뷰를_작성하려하면_커스텀예외를_발생시킨다(){
            // given
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
            given(authUserRepository.findByUsername(otherUser.getUsername())).willReturn(Optional.of(otherUser));
            // when, then
            assertThatThrownBy(() -> {
                reviewService.addReview(itemId, addReviewRequestDto, otherUser.getUsername());
            }).isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class UpdateReview{
        AddReviewRequestDto updateRequestDto = AddReviewRequestDto.builder()
                .title("제목 수정됨:제목은 10~255 글자여야 합니다.")
                .contents("내용 수정됨:내용은 10~255 글자여야 합니다.")
                .build();
        Review review;
        Long reviewId;

        @BeforeEach
        void init(){
            // item에 대해 authUser가 review를 작성
            review = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            reviewId = 1L;

            authUser.addReview(review);
            item.addReview(review);
        }

        @Test
        void 내가_작성한_리뷰를_수정한다(){
            // given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.ofNullable(authUser));
            // when
            ReviewResponseDto updatedReview = reviewService
                    .updateReview(itemId, reviewId, updateRequestDto, authUser.getUsername());
            // then
            assertThat(updatedReview.getTitle().equals(updateRequestDto.getTitle()));
            assertThat(updatedReview.getContents().equals(updateRequestDto.getContents()));
        }

        @Test
        void 내가_작성하지않은_리뷰를_수정하려고_요청을_보내면_커스텀예외를_발생시킨다(){
            //given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(otherUser.getUsername())).willReturn(Optional.of(otherUser));
            //when, then
            assertThatThrownBy(() -> {
                reviewService.updateReview(itemId, reviewId, updateRequestDto, otherUser.getUsername());
            }).isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class DeleteReview{
        Review review;
        Long reviewId;
        ReviewLikeUnit reviewLikeUnit;

        @BeforeEach
        void init(){
            // item에 대해 authUser가 review를 작성
            // otherUser가 review에 대해 좋아요를 누름
            review = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            reviewId = 1L;
            reviewLikeUnit = new ReviewLikeUnit();

            authUser.addReview(review);
            item.addReview(review);
            review.addReviewLikeUnit(reviewLikeUnit);
            otherUser.addReviewLikeUnit(reviewLikeUnit);
        }

        @Test
        void 내가_작성한_리뷰를_삭제한다(){
            //given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            given(reviewLikeUnitRepository.findAllByReview(review)).willReturn(Arrays.asList(reviewLikeUnit));
            //when
            reviewService.deleteReview(itemId, reviewId, authUser.getUsername());
            //then
            // review 삭제 확인
            assertThat(item.getReviewList().size()).isEqualTo(0);
            assertThat(authUser.getReviewList().size()).isEqualTo(0);
            // 해당 review의 좋아요 삭제 확인
            assertThat(review.getReviewLikeUnitList().size()).isEqualTo(0);
            assertThat(otherUser.getReviewLikeUnitList().size()).isEqualTo(0);
        }

        @Test
        void 다른유저가_작성한_리뷰_삭제는_커스텀예외를_발생시킨다(){
            //given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(otherUser.getUsername())).willReturn(Optional.of(otherUser));
            //when, then
            assertThatThrownBy(() -> {
                reviewService.deleteReview(itemId, reviewId, otherUser.getUsername());
            }).isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class LikeReview{
        Review review;
        Long reviewId;
        ReviewLikeUnit reviewLikeUnit;

        @BeforeEach
        void init(){
            // item에 대해 authUser가 review를 작성
            review = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            reviewId = 1L;
            reviewLikeUnit = new ReviewLikeUnit();

            authUser.addReview(review);
            item.addReview(review);
        }
        @Test
        void 내가_작성하지_않고_좋아요를_누르지_않은_리뷰에_좋아요를_누른다(){
            //given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(otherUser.getUsername())).willReturn(Optional.of(otherUser));
            given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, otherUser))
                    .willReturn(Optional.ofNullable(null));
            //when
            reviewService.likeReview(itemId, reviewId, otherUser.getUsername());
            //then
            assertThat(otherUser.getReviewLikeUnitList().size()).isEqualTo(1);
            assertThat(review.getReviewLikeUnitList().size()).isEqualTo(1);
        }

        @Test
        void 내가_작성한_리뷰에_좋아요를_누르면_커스텀예외가_발생한다(){
            //given
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, authUser))
                    .willReturn(Optional.ofNullable(null));
            //when, then
            assertThatThrownBy(() -> {
                reviewService.likeReview(itemId, reviewId, authUser.getUsername());
            }).isInstanceOf(CustomException.class);
        }

        @Test
        void 내가_좋아요를_누른_리뷰에서_좋아요를_취소한다(){
            //given
            //otherUser가 review에 좋아요 누름
            review.addReviewLikeUnit(reviewLikeUnit);
            otherUser.addReviewLikeUnit(reviewLikeUnit);
            given(reviewCacheProxy.findById(reviewId)).willReturn(Optional.of(review));
            given(authUserRepository.findByUsername(otherUser.getUsername())).willReturn(Optional.of(otherUser));
            given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, otherUser))
                    .willReturn(Optional.ofNullable(reviewLikeUnit));
            //when
            reviewService.removeLikeReview(itemId, reviewId, otherUser.getUsername());
            //then
            assertThat(otherUser.getReviewLikeUnitList().size()).isEqualTo(0);
            assertThat(review.getReviewLikeUnitList().size()).isEqualTo(0);
        }
    }
}