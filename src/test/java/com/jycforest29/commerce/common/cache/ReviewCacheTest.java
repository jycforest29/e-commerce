package com.jycforest29.commerce.common.cache;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewLikeUnitRepository;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.service.ReviewService;
import com.jycforest29.commerce.review.service.ReviewServiceImpl;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReviewCacheTest extends DockerComposeTestContainer {
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ReviewLikeUnitRepository reviewLikeUnitRepository;
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ReviewService reviewService;
    Item item = Item.builder()
            .name("test_item")
            .price(10000)
            .number(10)
            .build();
    Long itemId = 1L;
    Review review = Review.builder()
            .title("title")
            .contents("contents")
            .build();
    Long reviewId = 1L;
    AuthUser authUser = AuthUser.builder()
            .username("test_username")
            .password("test_password")
            .nickname("test_nickname")
            .build();

    @Nested
    class LocalCacheTest{
        @Nested
        class ReviewCache{
            @Test
            void reviewId를_통해_review를_가져올때_로컬_캐싱을_사용한다(){
                //given
                given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
                given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(review));

                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewService.getReviewDetail(item.getId(), review.getId()));
                //then
                verify(reviewRepository, atMostOnce()).findById(review.getId());
            }
        }

        @Nested
        class ReviewListByItemCache{
            @Test
            void itemId를_통해_reviewList를_가져올때_로컬_캐싱을_사용한다(){
                //given
                given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewService.getReviewListByItem(item.getId()));
                //then
                verify(reviewRepository, atMostOnce()).findAllByItem(item);
            }
        }
    }
}
