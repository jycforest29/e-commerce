package com.jycforest29.commerce.common.cache.local;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.proxy.ReviewCacheProxy;
import com.jycforest29.commerce.review.service.ReviewService;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
public class ReviewCacheTest extends DockerComposeTestContainer {
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private AuthUserRepository authUserRepository;
    @Autowired
    private ReviewCacheProxy reviewCacheProxy;
    @Autowired
    private ReviewService reviewService;

    AddReviewRequestDto addReviewRequestDto = AddReviewRequestDto.builder()
            .title("title")
            .contents("contents")
            .build();

    AuthUser authUser = AuthUser.builder()
            .username("test_username")
            .password("test_password")
            .nickname("test_nickname")
            .build();

    Item item = new Item(
            1L,
            "name",
            1000,
            1,
            new ArrayList<>()
    );

    Review review = new Review(
            1L,
            addReviewRequestDto.getTitle(),
            addReviewRequestDto.getContents(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            item,
            authUser,
            new ArrayList<>()
    );

    @Nested
    class LocalCacheTest{
        @Nested
        class ReviewListByItemCache{
            @Test
            void Cacheable_어노테이션을_테스트한다(){
                //given
                given(reviewRepository.findById(review.getId())).willReturn(Optional.ofNullable(review));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewCacheProxy.findById(review.getId()));
                //then
                verify(reviewRepository, atMostOnce()).findById(review.getId());
            }
            @Test
            void Review_추가하여_CachePut_어노테이션을_테스트한다(){
                //given
                OrderUnit orderUnit = OrderUnit.builder()
                        .item(item)
                        .number(1)
                        .build();
                MadeOrder.addOrderUnit(authUser, List.of(orderUnit));
                given(itemRepository.findById(1L))
                        .willReturn(Optional.ofNullable(item));
                given(authUserRepository.findByUsername(authUser.getUsername()))
                        .willReturn(Optional.ofNullable(authUser));
                //when
                reviewService.addReview(1L, addReviewRequestDto, authUser.getUsername());
                //then
                assertThat(reviewService.getReviewListByItem(1L).size()).isEqualTo(1);
                verify(reviewRepository, times(2)).findAllByItem(item);
            }

            @Test
            void Review_필드를_변경하여_CachePut_어노테이션을_테스트한다(){
                //given
                given(itemRepository.findById(1L))
                        .willReturn(Optional.ofNullable(item));
                given(reviewRepository.findById(1L))
                        .willReturn(Optional.ofNullable(review));
                given(authUserRepository.findByUsername(authUser.getUsername()))
                        .willReturn(Optional.ofNullable(authUser));
                AddReviewRequestDto updateReviewRequestDto = AddReviewRequestDto.builder()
                        .title("update_title")
                        .contents("update_contents")
                        .build();
                //when
                reviewService.updateReview(1L, 1L, updateReviewRequestDto, authUser.getUsername());
                //then
                assertThat(reviewService.getReviewListByItem(1L).get(0).getTitle())
                        .isEqualTo("update_title");
                verify(reviewRepository, times(2)).findAllByItem(item);
            }
        }
    }
}
