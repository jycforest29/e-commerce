package com.jycforest29.commerce.common.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class RedisLockRepository {
    private RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key){
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3000));


    }
    public Boolean unlock(Long key){
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key) {
        return key.toString();
    }

}


