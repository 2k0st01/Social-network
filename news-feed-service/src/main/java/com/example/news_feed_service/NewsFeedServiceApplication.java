package com.example.news_feed_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NewsFeedServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsFeedServiceApplication.class, args);
    }
}
