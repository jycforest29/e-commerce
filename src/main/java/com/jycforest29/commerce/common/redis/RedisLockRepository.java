package com.jycforest29.commerce.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;
    public Boolean lock(final Long key){
        return redisTemplate
                .opsForValue()
                // setnx 명령어 사용 (key, "lock")
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }
    public Boolean unlock(final Long key){
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(final Long key) {
        return key.toString();
    }

}


