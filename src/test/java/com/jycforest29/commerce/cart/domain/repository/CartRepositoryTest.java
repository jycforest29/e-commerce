package com.jycforest29.commerce.cart.domain.repository;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
class CartRepositoryTest {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void init(){
        authUserRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Transactional
    @Test
    public void 사용자_가입시_자동으로_초기화된_장바구니가_생성된다(){
        // given
        AuthUser authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        AuthUser savedAuthUser = authUserRepository.save(authUser);

        // when
        Long cardId = savedAuthUser.getCart().getId();
        Cart cart = cartRepository.findById(cardId).get();

        // that
        assertThat(cart.getTotalPrice()).isEqualTo(0);
    }
}