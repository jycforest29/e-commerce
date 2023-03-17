package com.jycforest29.commerce.common.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableCaching
public class CacheConfig {
    // EhCacheManagerFactoryBean은 Eh 캐시 매니저 생성 도우미로 CacheManaer의
    // 적절한 관리 및 인스턴스를 제공하는데 필요하며 EhCache 설정 리소스를 구성함.
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        return new EhCacheManagerFactoryBean();
    }
    // EhCacheCacheManager 등록
    @Bean("ehCacheManager")
    @Primary
    public CacheManager ehCacheManager(){
        // authUser의 key : AuthUser.username
        // cart의 key : AuthUser.username
        // reviewListByItem의 key : Item.id
        // review의 key : Review.id
        List<String> cacheNames = List.of("authUser", "cart", "reviewListByItem", "review");

        for(String cacheName: cacheNames){
            // 캐시 설정
            CacheConfiguration conf = new CacheConfiguration()
                    .eternal(false) // true일 경우 timeout 관련 설정이 무시. Element가 캐시에서 삭제되지 않음.
                    // Element가 지정한 시간동안 사용되지 않으면 캐시에서 제거됨. 이 값이 0인 경우 조회 관련 만료 시간을 정하지 않음.
                    .timeToIdleSeconds(0)
                    .timeToLiveSeconds(21600) // Element가 존재하는 시간. 이 값이 0인 경우 만료 시간을 정하지 않음.
                    .maxEntriesLocalHeap(0) // Heap 캐시 세모리 pool size 설정. 가비지 컬렉션의 대상이 됨.
                    .memoryStoreEvictionPolicy("LRU") // 캐시가 가득찼을때 관리 알고리즘 설정함. 기본은 LRU
                    .name(cacheName); // 캐시명.
            // 캐시 팩토리에 생성한 eh 캐시를 추가
            Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(new Cache(conf));
        }

        // 캐시 팩토리를 넘겨서 eh 캐시 매니저 생성
        return new EhCacheCacheManager(Objects.requireNonNull(cacheManagerFactoryBean().getObject()));
    }

    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration conf = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(21600));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }
}
