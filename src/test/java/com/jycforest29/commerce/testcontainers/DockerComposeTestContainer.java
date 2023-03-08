package com.jycforest29.commerce.testcontainers;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
public class DockerComposeTestContainer {
    @Container
    public static final DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("databases_1", 3306, Wait.forListeningPort())
            .withExposedService("redis_1", 6379, Wait.forListeningPort())
            .withLocalCompose(true);
}
