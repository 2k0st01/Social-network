package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtAuthFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import com.example.api_gateway.filter.JwtAuthFilter.Config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced
    public Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("*"));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "X-User-Id", "X-User-Email"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(Builder webClientBuilder) {
        return new JwtAuthFilter(webClientBuilder);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthFilter jwtAuthFilter) {
        return builder.routes()
                .route("eureka-client", r -> r.path("/sendMessenger", "/apis/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://EUREKA-CLIENT"))
                .route("eureka-friends-followers-service", r -> r.path("/ff/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://EUREKA-FRIENDS-FOLLOWERS-SERVICE"))
                .route("rating-service", r -> r.path("/rating/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://RATING-SERVICE"))
                .route("eureka-file-store", r -> r.path("/files/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://EUREKA-FILE-STORE"))
                .route("post-service", r -> r.path("/post/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://POST-SERVICE"))
                .route("search-history", r -> r.path("/search/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://SEARCH-HISTORY"))
                .route("news-feed-service", r -> r.path("/feed/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new Config())))
                        .uri("lb://NEWS-FEED-SERVICE"))
                .route("auth-service", r -> r.path("/date/**", "/scan/**", "/api/**")
                        .uri("lb://AUTH-SERVICE"))
                .build();
    }
}
