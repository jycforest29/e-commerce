package com.jycforest29.commerce.common.config.cache.local;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.review.controller.dto.AddReviewRequestDto;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.repository.ReviewRepository;
import com.jycforest29.commerce.review.service.ReviewServiceImpl;
import com.jycforest29.commerce.review.service.proxy.ReviewCacheProxy;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import com.jycforest29.commerce.utils.DockerComposeTestContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
public class ReviewCacheTest extends DockerComposeTestContainer {
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ReviewCacheProxy reviewCacheProxy;
    @Autowired
    private ReviewServiceImpl reviewService;

    @Nested
    class LocalCacheTest{
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
        class ReviewListByItemCache{
            @Test
            void Cacheable_어노테이션을_테스트한다(){
                //given
                given(itemRepository.findById(item.getId()))
                        .willReturn(Optional.ofNullable(item));
                given(reviewCacheProxy.findAllByItem(item))
                        .willReturn(Arrays.asList(review));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> reviewService.getReviewListByItem(item.getId()));
                //then
                verify(reviewCacheProxy, atMostOnce()).findAllByItem(item);
            }
            @Test
            void itemId에_대한_Review_1개를_추가하여_CachePut_어노테이션을_테스트한다(){
                //given
                OrderUnit orderUnit = OrderUnit.builder()
                        .item(item)
                        .number(1)
                        .build();
                MadeOrder.addOrderUnit(authUser, List.of(orderUnit));

                given(itemRepository.findById(item.getId()))
                        .willReturn(Optional.ofNullable(item));
                given(reviewRepository.findById(review.getId()))
                        .willReturn(Optional.ofNullable(review));
                given(authUserRepository.findByUsername(authUser.getUsername()))
                        .willReturn(Optional.ofNullable(authUser));
                given(reviewCacheProxy.findAllByItem(item))
                        .willReturn(Arrays.asList(review));
                //when
                reviewService.addReview(item.getId(), addReviewRequestDto, authUser.getUsername()); // 조회 1번 이하
                //then
                assertThat(reviewService.getReviewListByItem(item.getId()).size()).isEqualTo(1); // 조회 0번
                verify(reviewRepository, atMostOnce()).findAllByItem(item);
            }

            @Test
            void Review_필드를_변경하여_CachePut_어노테이션을_테스트한다(){
                //given
                AddReviewRequestDto updateReviewRequestDto = AddReviewRequestDto.builder()
                        .title("update_title")
                        .contents("update_contents")
                        .build();

                given(itemRepository.findById(item.getId()))
                        .willReturn(Optional.ofNullable(item));
                given(reviewRepository.findById(review.getId()))
                        .willReturn(Optional.ofNullable(review));
                given(reviewCacheProxy.findAllByItem(item))
                        .willReturn(Arrays.asList(review));
                given(authUserRepository.findByUsername(authUser.getUsername()))
                        .willReturn(Optional.ofNullable(authUser));
                //when
                // 조회 1번
                reviewService.updateReview(
                        item.getId(),
                        review.getId(),
                        updateReviewRequestDto,
                        authUser.getUsername()
                );
                // 조회 1번
                reviewService.updateReview(
                        item.getId(),
                        review.getId(),
                        updateReviewRequestDto,
                        authUser.getUsername()
                );
                //then
                assertThat(reviewService.getReviewListByItem(item.getId()).get(0).getTitle())
                        .isEqualTo("update_title"); // 조회 1번인데 캐싱됨 -> 0번
                verify(reviewCacheProxy, times(2)).findAllByItem(item);
            }
        }
    }
}
