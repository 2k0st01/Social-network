package com.example.eurekaclient.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class DataProcessor {
    @Value("${jwt.secret}")
    private String jwtToken;
    private final DiscoveryClient discoveryClient;
    private final WebClient.Builder webClientBuilder;

    public DataProcessor(DiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClientBuilder = webClientBuilder;
    }

    @Cacheable(value = "userExists", key = "#userId", unless = "#result == false")
    public boolean checkUserExists(String userId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("auth-service");
        if (instances.isEmpty()) {
            return false;
        }
        String url = instances.get(0).getUri().toString() + "/api/checkUserExist/" + userId;
        Boolean response = webClientBuilder
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        return Boolean.TRUE.equals(response);
    }

    @Cacheable(value = "userNames", key = "#firstId + ':' + #secondId", unless = "#result == null or #result.isEmpty()")
    public List<String> getUserNamesById(String firstId, String secondId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("auth-service");
        if (instances.isEmpty()) {
            return List.of();
        }
        String url = instances.get(0).getUri().toString() + "/api/getUsersNames?firstId=" + firstId + "&secondId=" + secondId;
        return webClientBuilder.build().get().uri(url).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .blockOptional().orElse(List.of());
    }

    @Cacheable(value = "token", key = "#username")
    public boolean isValidToken(String token, String username) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtToken)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String tokenUserId = claims.getSubject();
        return username.equals(tokenUserId);
    }
}
