package com.example.eureka_file_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EurekaFileStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaFileStoreApplication.class, args);
    }
}
