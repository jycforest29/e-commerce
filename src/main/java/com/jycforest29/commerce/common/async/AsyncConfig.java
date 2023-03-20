package com.jycforest29.commerce.common.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private int CORE_POOL_SIZE = 10;
    private int MAX_POOL_SIZE = 10;
    private int QUEUE_COMPACITY = 100;

    @Bean(name = "makeOrderUnitExecutor")
    public Executor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_COMPACITY);
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
