package com.jycforest29.commerce.common.cache.local;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.cart.service.CartServiceImpl;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CartCacheTest extends DockerComposeTestContainer {
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CartUnitRepository cartUnitRepository;
    @Autowired
    private CartServiceImpl cartService;

    AuthUser authUser = AuthUser.builder()
            .username("test_username")
            .password("test_password")
            .nickname("test_nickname")
            .build();

    Item item = Item.builder()
            .name("name")
            .price(1000)
            .number(1)
            .build();

    @Nested
    class LocalCacheTest{
        @Test
        void Cacheable_어노테이션을_테스트한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            //when
            IntStream.range(0, 10)
                    .forEach(i -> cartService.getCartUnitList(authUser.getUsername()));
            //then
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }

        @Test
        void CartUnit을_추가하여_CachePut_어노테이션을_테스트한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            given(itemRepository.findById(1L))
                    .willReturn(Optional.ofNullable(item));
            //when
            cartService.addCartUnitToCart(1L, 1, authUser.getUsername());
            //then - getCartUnitList은 @Cacheable로 조회함
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(1);
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }

        @Test
        void CartUnit을_삭제하여_CachePut_어노테이션을_테스트한다(){
            //given - 장바구니에 item(가격 1000원, 수량 1개), otherItem(가격 500원, 수량 1개) 추가함
            Item otherItem = Item.builder()
                    .name("other_item")
                    .price(500)
                    .number(1)
                    .build();

            CartUnit cartUnit = CartUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            cartUnitRepository.saveAndFlush(cartUnit);

            CartUnit otherCartUnit = CartUnit.builder()
                    .item(otherItem)
                    .number(1)
                    .build();
            authUser.getCart().addCartUnitToCart(otherCartUnit, otherItem.getPrice());
            cartUnitRepository.saveAndFlush(otherCartUnit);

            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            given(cartUnitRepository.findById(1L))
                    .willReturn(Optional.ofNullable(cartUnit));
            //when - item 제거
            cartService.deleteCartUnit(1L, authUser.getUsername());
            //then
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(1);
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getTotalPrice()).isEqualTo(500);
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }


        @Test
        void CacheEvict_어노테이션을_테스트한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            given(itemRepository.findById(1L))
                    .willReturn(Optional.ofNullable(item));
            //when
            cartService.addCartUnitToCart(1L, 1, authUser.getUsername());
            cartService.deleteCartAll(authUser.getUsername());
            //then
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(0);
            verify(authUserRepository, times(3)).findByUsername(authUser.getUsername());
        }
    }
}
