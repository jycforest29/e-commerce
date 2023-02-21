package com.jycforest29.commerce.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class RedisConfig {

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    // 스프링 데이터 Redis에서는 PlatformTransactionManager가 구현되어 있지 않음
    // 따라서 redis만 사용하려고 하면 @Transactional을 통한 트랜잭션 지원 불가
    // 따라서 DataSource, TransactionManager를 빈으로 등록하고 트랜잭션을 사용할 수 있도록
    // 설정 필요함.
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
        return jpaTransactionManager;
    }
}
