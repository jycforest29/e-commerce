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
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        return new EhCacheManagerFactoryBean();
    }

    @Bean("ehCacheManager")
    @Primary
    public CacheManager ehCacheManager(){
        // cart 캐시의 key : AuthUser.username
        // reviewListByItem 캐시의 key : Item.id
        List<String> cacheNames = List.of("cart", "reviewListByItem");

        for(String cacheName: cacheNames){
            CacheConfiguration conf = new CacheConfiguration()
                    .eternal(false)
                    .timeToIdleSeconds(0)
                    .timeToLiveSeconds(21600)
                    .maxEntriesLocalHeap(10000)
                    .memoryStoreEvictionPolicy("LRU")
                    .name(cacheName);

            // 캐시 팩토리에 생성한 eh 캐시를 추가
            Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(new Cache(conf));
        }

        return new EhCacheCacheManager(Objects.requireNonNull(cacheManagerFactoryBean().getObject()));
    }

    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        // ActiveUser 캐시의 key : AuthUser.username
        RedisCacheConfiguration conf = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(60));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }
}
