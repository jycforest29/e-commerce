package com.jycforest29.commerce.common.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;

@Configuration
public class TimeConfig {
    @Bean
    public Clock clock(){
        return Clock.systemDefaultZone();
    }
}
