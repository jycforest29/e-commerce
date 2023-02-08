package com.jycforest29.commerce.review.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private AuthUser authUser;
    private AuthUser otherUser;
    private AddReviewRequestDTO addReviewRequestDTO;
    private AddReviewRequestDTO updateRequestDTO;
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

        addReviewRequestDTO = AddReviewRequestDTO.builder()
                .title("제목:제목은 10~255 글자여야 합니다.")
                .contents("내용:내용은 10~255 글자여야 합니다.")
                .build();

        updateRequestDTO = AddReviewRequestDTO.builder()
                .title("제목 수정됨:제목은 10~255 글자여야 합니다.")
                .contents("내용 수정됨:내용은 10~255 글자여야 합니다.")
                .build();

        review = Review.builder()
                .title(addReviewRequestDTO.getTitle())
                .contents(addReviewRequestDTO.getContents())
                .item(item)
                .authUser(authUser)
                .build();
        review.setId(1L);

        reviewLikeUnit = ReviewLikeUnit.builder()
                .review(review)
                .authUser(authUser)
                .build();
    }

    @Test
    void 클릭한_아이템의_모든_리뷰가_리턴된다(){
        // given
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(reviewRepository.findAllByItemId(item.getId())).willReturn(Arrays.asList(review));
        //when
        List<Review> result = reviewService.getReviewListByItemId(item.getId());
        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).equals(review));
    }

    @Test
    void 내가_구매한_아이템에_대해_리뷰를_작성한다(){ // !현재 모든 아이템에 가능함.
        // given
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        // when
        reviewService.addReview(item.getId(), addReviewRequestDTO, authUser.getId());
        //then
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
    }

    @Test
    void 내가_작성한_리뷰를_수정한다(){
        // given
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        // when
        Review updatedReview = reviewService.updateReview(item.getId(), authUser.getId(), review.getId(), updateRequestDTO);
        // then
        assertThat(updatedReview.getTitle().equals(updateRequestDTO.getTitle()));
        assertThat(updatedReview.getContents().equals(updateRequestDTO.getContents()));
    }

    @Test
    void 내가_작성하지않은_리뷰를_수정하려고_요청을_보내면_커스텀예외를_발생시킨다(){
        //given
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(authUserRepository.findById(otherUser.getId())).willReturn(Optional.of(otherUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.updateReview(item.getId(), otherUser.getId(), review.getId(), updateRequestDTO);
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_작성한_리뷰를_삭제한다(){
        //given
        item.getReviewList().add(review);
        authUser.getReviewList().add(review);
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        //when
        reviewService.deleteReview(item.getId(), authUser.getId(), review.getId());
        //then
        assertThat(item.getReviewList().size()).isEqualTo(0);
        assertThat(authUser.getReviewList().size()).isEqualTo(0);
    }

    @Test
    void 다른유저가_작성한_리뷰_삭제는_커스텀예외를_발생시킨다(){
        //given
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(authUserRepository.findById(otherUser.getId())).willReturn(Optional.of(otherUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.deleteReview(item.getId(), otherUser.getId(), review.getId());
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_좋아요를_누르지_않은_리뷰에_좋아요를_누른다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(reviewLikeUnitRepository.findByAuthUserIdAndReviewId(authUser.getId(), review.getId()))
                .willReturn(Optional.ofNullable(null));
        //when
        reviewService.likeReview(authUser.getId(), review.getId());
        //then
        assertThat(authUser.getReviewLikeUnitList().size()).isEqualTo(1);
        assertThat(review.getReviewLikeUnitList().size()).isEqualTo(1);
    }

    @Test
    void 내가_좋아요를_누른_리뷰에_좋아요를_누르면_커스텀예외를_발생시킨다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(reviewLikeUnitRepository.findByAuthUserIdAndReviewId(authUser.getId(), review.getId()))
                .willReturn(Optional.ofNullable(reviewLikeUnit));
        //when, then
        assertThatThrownBy(() -> {
            reviewService.likeReview(authUser.getId(), review.getId());
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 내가_좋아요를_누른_리뷰에_좋아요를_취소한다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        authUser.addReviewLikeUnit(reviewLikeUnit);
        review.addReviewLikeUnit(reviewLikeUnit);
        given(reviewLikeUnitRepository.findByAuthUserIdAndReviewId(authUser.getId(), review.getId()))
                .willReturn(Optional.ofNullable(reviewLikeUnit));
        //when
        reviewService.removeLikeReview(authUser.getId(), review.getId());
        //then
        assertThat(authUser.getReviewLikeUnitList().size()).isEqualTo(0);
        assertThat(review.getReviewLikeUnitList().size()).isEqualTo(0);
    }


}