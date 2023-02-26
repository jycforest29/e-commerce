package com.jycforest29.commerce.common.cache;

import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.cart.service.CartServiceImpl;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
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
public class CartCacheTest {
    @MockBean
    private CartUnitRepository cartUnitRepository;
    @MockBean
    private AuthUserRepository authUserRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private CartServiceImpl cartService;

    @Nested
    class LocalCacheTest{
        AuthUser authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        Long authUserId = 1L;

        @Test
        void authUserId를_통해_authUser를_가져올때_로컬_캐싱을_사용한다(){
            //given
            given(authUserRepository.findById(authUserId)).willReturn(Optional.ofNullable(authUser));
            //when
            IntStream.range(0, 10)
                    .forEach(i -> cartService.getAuthUser(authUserId));
            //then
            verify(authUserRepository, atMostOnce()).findById(authUserId);
        }
    }

    @Nested
    class GlobalCacheTest{
        Item item = Item.builder()
                .name("test_item")
                .price(10000)
                .number(100)
                .build();
        Long itemId = 1L;

        @Test
        void itemId를_통해_item을_가져올때_전역_캐싱을_사용한다(){
            //given
            given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
            //when
            IntStream.range(0, 10)
                    .forEach(i -> cartService.getItem(itemId));
            //then
            verify(itemRepository, atMostOnce()).findById(itemId);
        }
    }
}
