package com.jycforest29.commerce.cart.domain.repository;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
class CartUnitRepositoryTest {
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartUnitRepository cartUnitRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void init(){
        itemRepository.deleteAll();
        authUserRepository.deleteAll();
        cartUnitRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Transactional
    @Test
    public void 한_카트에_담긴_아이템_리스트를_삭제한다(){
        // given
        AuthUser authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        AuthUser savedAuthUser = authUserRepository.save(authUser);

        Item item = Item.builder()
                .name("test_name")
                .price(1000)
                .number(10)
                .build();
        Item savedItem = itemRepository.save(item);

        List<Long> toDelete = new ArrayList<>();
        IntStream.range(0, 10).forEach(s -> {
            CartUnit cartUnit = CartUnit.builder()
                    .item(savedItem)
                    .number(1)
                    .build();

            cartUnit.setCart(savedAuthUser.getCart());
            toDelete.add(cartUnitRepository.save(cartUnit).getId());
        });

        // when
        cartUnitRepository.deleteAllByCartUnitIdList(toDelete);

        // then
        assertThat(cartUnitRepository.findAll().size()).isEqualTo(0);
    }
}