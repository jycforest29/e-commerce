package com.jycforest29.commerce.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class DockerComposeTestContainer{
    private static GenericContainer<?> REDIS_CONTAINER;
    private static GenericContainer<?> MYSQL_CONTAINER;
    private static String REDIS_IMAGE = "redis:latest";
    private static String MYSQL_IMAGE = "mysql:latest";

//    static {
//        MYSQL_CONTAINER = new GenericContainer<>(MYSQL_IMAGE)
//                .withExposedPorts(3306)
//                .withReuse(true);
//        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
//                .withExposedPorts(6379)
//                .withReuse(true);
//        MYSQL_CONTAINER.start();
//        REDIS_CONTAINER.start();
//    }

//    @DynamicPropertySource
//    public static void setProps(DynamicPropertyRegistry registry){
//        registry.add("spring.redis.host", () -> REDIS_CONTAINER.getHost());
//        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
//        log.info("레디스 호스트"+REDIS_CONTAINER.getHost());
//        log.info("레디스 포트"+REDIS_CONTAINER.getMappedPort(6379));
//    }

    @BeforeAll
    public static void setUp() throws Exception {
        MYSQL_CONTAINER = new GenericContainer<>(MYSQL_IMAGE)
                .withExposedPorts(3306)
                .withReuse(true);
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true);
        MYSQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(6379)));
    }
}
