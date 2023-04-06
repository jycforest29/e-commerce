package com.jycforest29.commerce.testcontainers;

import org.junit.ClassRule;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class DockerComposeTestContainer {

    @Container
    private static GenericContainer<?> REDIS_CONTAINER;
    @Container
    private static GenericContainer<?> MYSQL_CONTAINER;
    private static String REDIS_IMAGE = "redis:latest";
    private static String MYSQL_IMAGE = "mysql:latest";

    static {
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true);
        MYSQL_CONTAINER = new GenericContainer<>(MYSQL_IMAGE)
                .withExposedPorts(3306)
                .withReuse(true);
        REDIS_CONTAINER.start();
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dropProps(DynamicPropertyRegistry registry){
        registry.add("spring.redis.host", () -> REDIS_CONTAINER.getHost());
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }
}
