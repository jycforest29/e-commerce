package com.jycforest29.commerce.common.cache;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.service.ReviewServiceImpl;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReviewCacheTest {

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @MockBean
    private AuthUserRepository authUserRepository;
    @Autowired
    private ReviewServiceImpl reviewService;

    AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
            .title("제목:제목은 10~255 글자여야 합니다.")
            .contents("내용:내용은 10~255 글자여야 합니다.")
            .build();

    @Nested
    class LocalCacheTest{
        @Nested
        class ReviewCache{
            Review review = Review.builder()
                    .title(addReviewRequestDto.getTitle())
                    .contents(addReviewRequestDto.getContents())
                    .build();
            Long reviewId = 1L;

            @Test
            void reviewId를_통해_review를_가져올때_로컬_캐싱을_사용한다(){
                //given
                given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(review));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewService.getReview(reviewId));
                //then
                verify(reviewRepository, atMostOnce()).findById(reviewId);
            }
        }

        @Nested
        class ReviewListByItemCache{
            Item item = Item.builder()
                    .name("test_item")
                    .price(10000)
                    .number(10)
                    .build();
            Long itemId = 1L;

            @Test
            void itemId를_통해_review_리스트를_가져올때_로컬_캐싱을_사용한다(){
                //given
                given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewService.getReviewListByItem(itemId));
                //then
                verify(reviewRepository, atMostOnce()).findAllByItem(item);
            }
        }
    }
}
