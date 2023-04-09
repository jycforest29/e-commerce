package com.jycforest29.commerce.common.cache.global;

import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import com.jycforest29.commerce.user.proxy.AuthUserCacheProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(properties = "spring.profiles.active:test")
public class AuthUserCacheTest extends DockerComposeTestContainer {
    @MockBean
    private AuthUserRepository authUserRepository;
    @Autowired
    private AuthUserCacheProxy authUserCacheProxy;
    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    AuthUser authUser = AuthUser.builder()
            .username("test_username")
            .password("test_password")
            .nickname("test_nickname")
            .build();

    @Nested
    class GlobalCacheTest {
        @Nested
        class AuthUserCache {
            @Test
            void Cacheable_어노테이션을_테스트한다() {
                //given
                given(authUserRepository.findByUsername(authUser.getUsername()))
                        .willReturn(Optional.ofNullable(authUser));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> authUserCacheProxy.findByUsername(authUser.getUsername()));
                //then
                verify(authUserRepository, atMostOnce()).findByUsername(authUser.getUsername());
            }
        }
    }
}
