package com.jycforest29.commerce.common.cache.local;

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

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
    @PersistenceContext
    private EntityManager entityManager;

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
            //when, then
            cartService.addCartUnitToCart(1L, 1, authUser.getUsername());
            //then
            assertThat(cartService.getCartUnitList(authUser.getUsername())).isEqualTo(1);
        }

        @Test
        void CartUnit을_삭제하여_CachePut_어노테이션을_테스트한다(){
            //given
            given(itemRepository.findById(1L))
                    .willReturn(Optional.ofNullable(item));
            //when
            cartService.addCartUnitToCart(1L, 1, authUser.getUsername());
            //then
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }


        @Test
        void CacheEvict_어노테이션을_테스트한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername()))
                    .willReturn(Optional.ofNullable(authUser));
            //when
            IntStream.range(0, 10)
                    .forEach(i -> cartService.getCartUnitList(authUser.getUsername()));
            //then
            verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
        }
    }
}
