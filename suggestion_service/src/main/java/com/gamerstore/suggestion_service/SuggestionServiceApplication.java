package com.gamerstore.suggestion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SuggestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                SuggestionServiceApplication.class,
                args
        );
    }
}