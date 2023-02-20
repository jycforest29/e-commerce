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
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @Mock
    private AuthUserRepository authUserRepository;
    @InjectMocks
    private ReviewServiceImpl reviewService;
    private Item item;
    private Long itemId = 1L;
    private AuthUser authUser;
    private Long authUserId = 1L;
    private AuthUser otherUser;
    private Long otherUserId = 2L;
    private AddReviewRequestDto addReviewRequestDto;
    private Review review;
    private Long reviewId = 1L;
    private Review more_liked_review;
    private ReviewLikeUnit reviewLikeUnit;
    private MadeOrder madeOrder;
    private OrderUnit orderUnit;

    @BeforeEach
    void init(){
        // 기본 엔티티만 생성
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
                .username("test_username_other")
                .password("test_password_other")
                .nickname("test_nickname_other")
                .build();

        addReviewRequestDto = AddReviewRequestDto.builder()
                .title("제목:제목은 10~255 글자여야 합니다.")
                .contents("내용:내용은 10~255 글자여야 합니다.")
                .build();

        review = Review.builder()
                .title(addReviewRequestDto.getTitle())
                .contents(addReviewRequestDto.getContents())
                .build();

        more_liked_review = Review.builder()
                .title(addReviewRequestDto.getTitle())
                .contents(addReviewRequestDto.getContents())
                .build();

        reviewLikeUnit = new ReviewLikeUnit();

        madeOrder = MadeOrder.builder()
                    .authUser(authUser)
                    .build();

        orderUnit = OrderUnit.builder()
                .item(item)
                .number(1)
                .build();

        // 연관관계 매핑
        // 다대일의 정합성 위해 -> DB 계층까지 생각하면 안되고 MOCK으로 작동할 수 있게끔 해야
        // authUser는 item을 구매했기에 review 작성 가능
        madeOrder.setOrderUnitList(Arrays.asList(orderUnit));
        // item에 대한 review의 작성자는 authUser
        authUser.addReview(review);
        item.addReview(review);
        // item에 대한 more_liked_review의 작성자는 otherUser
        otherUser.addReview(more_liked_review);
        item.addReview(more_liked_review);
        otherUser.addReviewLikeUnit(reviewLikeUnit);
        review.addReviewLikeUnit(reviewLikeUnit);
    }

    @Test
    void authUserId를_통해_authUser를_가져올때_캐싱을_사용한다(){
        //given
        given(authUserRepository.findById(authUserId)).willReturn(Optional.ofNullable(authUser));
        //when
        IntStream.range(0, 10)
                .forEach(i -> reviewService.getAuthUser(authUserId));
        //then
        verify(reviewService.getAuthUser(authUserId), times(1));
    }

    @Test
    void 클릭한_아이템의_모든_리뷰가_리뷰의_좋아요개수_기준으로_내림차순_정렬되어_리턴된다(){
        // given
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(reviewRepository.findAllByItem(item)).willReturn(Arrays.asList(review));
        //when
        List<ReviewResponseDto> result = reviewService.getReviewListByItem(itemId);
        //then
        assertThat(result).isEqualTo(Arrays.asList(more_liked_review, review));
    }

    @Test
    void 내가_구매한_아이템에_대해_리뷰를_작성한다(){
        // given
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        // when
        reviewService.addReview(itemId, addReviewRequestDto, authUserId);
        //then
        assertThat(item.getReviewList().size()).isEqualTo(1);
        assertThat(authUser.getReviewList().size()).isEqualTo(1);
    }

    @Test
    void 내가_구매하지_않은_아이템에_대해_리뷰를_작성하려하면_커스텀예외를_발생시킨다(){
        // given
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        // when, then
        assertThatThrownBy(() -> {
            reviewService.addReview(itemId, addReviewRequestDto, authUserId);
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_작성한_리뷰를_수정한다(){
        // given
        AddReviewRequestDto updateRequestDto = AddReviewRequestDto.builder()
                .title("제목 수정됨:제목은 10~255 글자여야 합니다.")
                .contents("내용 수정됨:내용은 10~255 글자여야 합니다.")
                .build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        // when
        ReviewResponseDto updatedReview = reviewService.updateReview(itemId, reviewId, updateRequestDto, authUserId);
        // then
        assertThat(updatedReview.getTitle().equals(updateRequestDto.getTitle()));
        assertThat(updatedReview.getContents().equals(updateRequestDto.getContents()));
    }

    @Test
    void 내가_작성하지않은_리뷰를_수정하려고_요청을_보내면_커스텀예외를_발생시킨다(){
        //given
        AddReviewRequestDto updateRequestDto = AddReviewRequestDto.builder()
                .title("제목 수정됨:제목은 10~255 글자여야 합니다.")
                .contents("내용 수정됨:내용은 10~255 글자여야 합니다.")
                .build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.updateReview(itemId, reviewId, updateRequestDto, otherUserId);
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_작성한_리뷰를_삭제한다(){
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        //when
        reviewService.deleteReview(itemId, reviewId, authUserId);
        //then
        assertThat(authUser.getReviewLikeUnitList().stream()
                .filter(s -> s.getReview().equals(review))
                .collect(Collectors.toList())
                .size()).isEqualTo(0);
        assertThat(item.getReviewList().size()).isEqualTo(0);
        assertThat(authUser.getReviewList().size()).isEqualTo(0);
    }

    @Test
    void 다른유저가_작성한_리뷰_삭제는_커스텀예외를_발생시킨다(){
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.deleteReview(itemId, reviewId, otherUserId);
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_작성하지_않고_좋아요를_누르지_않은_리뷰에_좋아요를_누른다(){
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));
        given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, otherUser))
                .willReturn(Optional.ofNullable(null));
        //when
        reviewService.likeReview(itemId, reviewId, otherUserId);
        //then
        assertThat(otherUser.getReviewLikeUnitList().size()).isEqualTo(1);
        assertThat(review.getReviewLikeUnitList().size()).isEqualTo(1);
    }

    @Test
    void 내가_작성한_리뷰에_좋아요를_누르면_커스텀예외가_발생한다(){
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
        given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, authUser))
                .willReturn(Optional.ofNullable(null));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.likeReview(itemId, reviewId, authUserId);
        }).isInstanceOf(CustomException.class);
    }
    @Test
    void 내가_좋아요를_누른_리뷰에서_좋아요를_취소한다(){
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(authUserRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));
        given(reviewLikeUnitRepository.findByReviewAndAuthUser(review, otherUser))
                .willReturn(Optional.ofNullable(reviewLikeUnit));
        //when
        reviewService.removeLikeReview(itemId, reviewId, authUserId);
        //then
        assertThat(authUser.getReviewLikeUnitList().size()).isEqualTo(0);
        assertThat(review.getReviewLikeUnitList().size()).isEqualTo(0);
    }
}