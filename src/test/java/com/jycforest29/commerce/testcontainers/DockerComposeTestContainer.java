package com.jycforest29.commerce.testcontainers;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@ActiveProfiles("test") // application-test.yml을 사용하도록 세팅함
// 설정한 프로퍼티에 따라 데이터소스가 적용되므로 none을 사용하여 docker mysql 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class DockerComposeTestContainer {
    @Container
    public static final DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("databases_1", 3306, Wait.forListeningPort())
            .withExposedService("redis_1", 6379, Wait.forListeningPort())
            .withLocalCompose(true);
}
