package com.tinqinacademy.authentication.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("com.tinqinacademy.authentication.persistance.repositories")
@EntityScan(basePackages = "com.tinqinacademy.authentication.persistance.entities")
@ComponentScan(basePackages = "com.tinqinacademy.authentication")
@EnableScheduling
@EnableAsync
public class AuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

}
