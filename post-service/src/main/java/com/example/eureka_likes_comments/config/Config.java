package com.example.eureka_likes_comments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
