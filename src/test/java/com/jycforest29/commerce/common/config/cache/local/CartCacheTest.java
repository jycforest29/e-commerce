package com.jycforest29.commerce.common.config.cache.local;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.cart.service.CartServiceImpl;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.utils.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
public class CartCacheTest extends DockerComposeTestContainer {
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CartUnitRepository cartUnitRepository;
    @Autowired
    private CartServiceImpl cartService;

    @Nested
    class LocalCacheTest{
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

        Long itemId = 1L;

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
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item));
            //when
            cartService.addCartUnitToCart(itemId, 1, authUser.getUsername()); // 조회 1번
            //then - getCartUnitList은 @Cacheable로 조회함
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(1); // 조회 1번이어야 되는데 캐싱됨 -> 0번
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }

        @Test
        void CartUnit을_삭제하여_CachePut_어노테이션을_테스트한다(){
            //given - 장바구니에 item(가격 1000원, 수량 1개), otherItem(가격 500원, 수량 1개) 추가함
            CartUnit cartUnit = CartUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            사용자의_카트에_카트_유닛을_추가(authUser, cartUnit, item);

            Item otherItem = Item.builder()
                    .name("other_item")
                    .price(500)
                    .number(1)
                    .build();
            CartUnit otherCartUnit = CartUnit.builder()
                    .item(otherItem)
                    .number(1)
                    .build();
            사용자의_카트에_카트_유닛을_추가(authUser, otherCartUnit, otherItem);

            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            given(cartUnitRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(cartUnit));
            //when - cartUnit 제거
            cartService.deleteCartUnit(itemId, authUser.getUsername()); // 조회 1번
            //then
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(1); // 조회 1번이어야 되는데 캐싱됨 -> 0번
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getTotalPrice())
                    .isEqualTo(500); // 조회 1번이어야 되는데 캐싱됨 -> 0번
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }

        @Test
        void CacheEvict_어노테이션을_테스트한다(){
            //given
            CartUnit cartUnit = CartUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            사용자의_카트에_카트_유닛을_추가(authUser, cartUnit, item);

            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item));
            //when
            cartService.deleteCartAll(authUser.getUsername()); // 1번
            //then
            assertThat(cartService.getCartUnitList(authUser.getUsername()).getCartUnitResponseDtoList().size())
                    .isEqualTo(0); // 캐시 모두 삭제했으므로 1번
            verify(authUserRepository, times(2)).findByUsername(authUser.getUsername());
        }

        @Transactional
        void 사용자의_카트에_카트_유닛을_추가(AuthUser authUser, CartUnit cartUnit, Item item){
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            cartUnitRepository.saveAndFlush(cartUnit);
        }
    }
}
