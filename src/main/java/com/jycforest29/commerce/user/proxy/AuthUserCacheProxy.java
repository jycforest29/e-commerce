package com.jycforest29.commerce.user.proxy;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUserCacheProxy {
    private final AuthUserRepository authUserRepository;

    @Cacheable(value = "authUser", key = "#username", cacheManager = "redisCacheManager")
    public Optional<AuthUser> findByUsername(String username){
        return authUserRepository.findByUsername(username);
    }
}
