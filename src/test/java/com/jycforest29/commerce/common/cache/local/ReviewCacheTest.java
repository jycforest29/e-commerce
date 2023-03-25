package com.jycforest29.commerce.common.cache.local;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.proxy.ReviewCacheProxy;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ReviewCacheTest extends DockerComposeTestContainer {
    @MockBean
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewCacheProxy reviewCacheProxy;

    // reviewId와 review는 개별로 생각해도 됨
    Long reviewId = 1L;
    Review review = Review.builder()
            .title("title")
            .contents("contents")
            .build();

    Long itemId = 1L;
    Item item = new Item(
            itemId,
            "name",
            1000,
            1,
            new ArrayList<>()
    );

    @Nested
    class LocalCacheTest{
//        @Nested
//        class ReviewCache{
//            @Test
//            void reviewId를_통해_review를_가져올때_로컬_캐싱을_사용한다(){
//                //given
//                given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(review));
//                //when
//                IntStream.range(0, 10)
//                        .forEach(i -> reviewCacheProxy.findById(reviewId));
//                //then
//                verify(reviewRepository, atMostOnce()).findById(reviewId);
//            }
//        }

        @Nested
        class ReviewListByItemCache{
            @Test
            void itemId를_통해_reviewList를_가져올때_로컬_캐싱을_사용한다(){
                //given
                given(reviewRepository.findAllByItem(item)).willReturn(Arrays.asList(review));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewCacheProxy.findAllByItem(item));
                //then
                verify(reviewRepository, atMostOnce()).findAllByItem(item);
            }
        }
    }
}
