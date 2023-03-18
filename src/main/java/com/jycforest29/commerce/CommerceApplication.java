package com.jycforest29.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ComponentScan(basePackages = "com.jycforest29.commerce")
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class CommerceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommerceApplication.class, args);
	}

}
