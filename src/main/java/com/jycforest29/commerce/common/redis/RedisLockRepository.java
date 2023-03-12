package com.jycforest29.commerce.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key){
        return redisTemplate.opsForValue().setIfAbsent(key.toString(), "lock", Duration.ofMillis(3_000));
    }

    // redis의 multi-exec을 사용해 배치 단위로 커맨드 실행.
    // -> @Transactional 사용함
    // @Transactional은 thread local 기반이므로 reactive 환경에서는 동작하지 않음. reactive 환경에서 @Transactional을
    // 사용하기 위해서는 redisson 사용해야

    // redis는 싱글 스레드 기반으로 데이터를 처리하지만 여러 명의 클라이언트 요청에 동시에 응답하는 동시성도 갖고 있음
    // 유저 레벨에서는 싱글 스레드로 동작하지만 커널 I/O 레벨에서는 스레드 풀 이용
    @Transactional
    public Boolean lock(List<Long> key){
        return redisTemplate.execute(new SessionCallback<Boolean>() {
                    @Override
                    public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                        Map<K, V> keyMap = new HashMap<>();
                        key.stream().forEach(s -> keyMap.put((K) s.toString(), (V) "lock"));
                        if(!operations.opsForValue().multiSetIfAbsent(keyMap)){
                            return false;
                        }
                        operations.multi();
                        key.stream().forEach(s -> operations.expire((K) s.toString(), Duration.ofMillis(3_000)));
                        operations.exec();
                        return true;
//                        operations.multi();
//                        for(K k : keyToK){
//                            // operations.opsForValue()를 통해 레디스의 string 객체에 접근 가능함
//                            operations.opsForValue().setIfAbsent(k, (V) "lock", Duration.ofMillis(3_000));
//                        }
//                        // 한번 watch를 선언한 key는 exec가 실행되면 즉시 unwatch 상태로 변경됨
//                        // 각각의 키별로 unwatch를 직접 선언할 수 없음 주의
//                        operations.exec();
                    }
                });
    }

    @Transactional
    public Boolean unlock(List<Long> key){
        // delete 사용시 O(N), unlink 사용시 O(1)(즉 key의 개수에 상관 없음)
        // unlink : 실제로는 keyspace에서 key를 제거하고 메모리 상의 삭제는 다른 스레드에서 비동기로 이루어짐
        redisTemplate.unlink(key.stream()
                .map(s -> s.toString())
                .collect(Collectors.toList())
        );
        return true;
//        return redisTemplate
//                .execute(new SessionCallback<Boolean>() {
//                    @Override
//                    public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
//                        operations.multi();
//                        for(Long k : key){
//                            logger.info(k.toString()+"원소에 대한 배치 락 해제");
//                            operations.delete((K) k.toString());
//                        }
//                        operations.exec();
//                        return true;
//                    }
//                });
    }

    @Transactional
    public Boolean unlock(Long key){
        return redisTemplate.delete(key.toString());
    }
}


