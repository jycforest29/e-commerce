package com.jycforest29.commerce.common.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }

    @Bean("ehCacheManager")
    @Primary
    public CacheManager ehCacheManager(){
        // cart 캐시의 key : AuthUser.username
        // reviewListByItem 캐시의 key : Item.id

//        List<String> cacheNames = List.of("cart", "review");
//
//        for(String cacheName: cacheNames){
//            CacheConfiguration conf = new CacheConfiguration()
//                    .eternal(false)
//                    .timeToIdleSeconds(0)
//                    .timeToLiveSeconds(21600)
//                    .maxEntriesLocalHeap(10000)
//                    .memoryStoreEvictionPolicy("LRU")
//                    .name(cacheName);
//
//            // 캐시 팩토리에 생성한 eh 캐시를 추가
//            Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(new Cache(conf));
//        }
//        return new EhCacheCacheManager(Objects.requireNonNull(cacheManagerFactoryBean().getObject()));
        return new EhCacheCacheManager(cacheManagerFactoryBean().getObject());
    }

    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        // AuthUser 캐시의 key : AuthUser.username
        RedisCacheConfiguration conf = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(60));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }
}
