package com.jycforest29.commerce.user.domain.repository;

import com.jycforest29.commerce.cart.domain.repository.CartRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
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
class AuthUserRepositoryTest {

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
    public void username으로_authUser를_조회한다(){
        // given
        AuthUser authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        AuthUser savedAuthUser = authUserRepository.save(authUser);

        // when
        AuthUser foundAuthUser = authUserRepository.findByUsername(authUser.getUsername()).get();

        // then
        assertThat(foundAuthUser.getUsername()).isEqualTo(savedAuthUser.getUsername());
        // TODO: 단순히 persistence context에 바로 save를 하지 않았기 때문에 암호화되지 않았다고 생각함. 이 부분 확인.
        assertThat(foundAuthUser.getPassword()).isEqualTo(savedAuthUser.getPassword());
        assertThat(foundAuthUser.getNickname()).isEqualTo(savedAuthUser.getNickname());
    }

    @Transactional
    @Test
    public void 입력된_username이_중복되었는지_확인한다(){
        // given
        AuthUser authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        authUserRepository.save(authUser);

        // when
        AuthUser newAuthUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();

        // then
        assertThat(authUserRepository.existsByUsername(newAuthUser.getUsername())).isEqualTo(true);
    }
}