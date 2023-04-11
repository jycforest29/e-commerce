package com.jycforest29.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan(basePackages = "com.jycforest29.commerce")
//@EnableScheduling
//@EnableBatchProcessing
@EnableAsync
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class CommerceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommerceApplication.class, args);
	}
}
