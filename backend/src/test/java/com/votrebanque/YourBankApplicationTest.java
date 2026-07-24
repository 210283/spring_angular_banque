package com.votrebanque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class YourBankApplicationTest {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        // Starts an official PostgreSQL container using the Alpine (lightweight) image.
        return new PostgreSQLContainer("postgres:16-alpine");
    }

    public static void main(String[] args) {
        // Launch the main application, injecting this container configuration into it.
        SpringApplication.from(VotrebanqueApplication::main)
                .with(VotrebanqueApplication.class)
                .run(args);
    }
}
