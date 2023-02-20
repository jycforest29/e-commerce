package com.jycforest29.commerce.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key){
        return redisTemplate.opsForValue()
                .setIfAbsent(key.toString(), "lock", Duration.ofMillis(3000));
    }

    // redis의 multi-exec을 사용해 배치 단위로 커맨드 실행.
    // -> @Transactional 사용함
    // @Transactional은 thread local 기반이므로 reactive 환경에서는 동작하지 않음. reactive 환경에서 @Transactional을
    // 사용하기 위해서는 redisson 사용해야
    public Boolean lock(Set<Long> key){
        return redisTemplate
                .execute(new SessionCallback<Boolean>() {
                    @Override
                    public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                        operations.multi();
                        for(Long k : key){
                            operations.opsForValue().setIfAbsent((K) k.toString(), (V) "lock", Duration.ofMillis(3000));
                        }
                        operations.exec();
                        return true;
                    }
                });
    }

    public Boolean unlock(Long key){
        return redisTemplate.delete(key.toString());
    }

    public Long unlock(Set<Long> key){
        return redisTemplate.delete(key.stream()
                .map(s -> s.toString())
                .collect(Collectors.toSet())
        );
    }
}


