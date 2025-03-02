package com.example.eureka_friends_followers_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import static io.jsonwebtoken.Jwts.header;

@Component
@RequiredArgsConstructor
public class DataProcessor {
    @Value(value = "${jwt.secret}")
    private String jwtToken;
    private final DiscoveryClient discoveryClient;
    private final Builder webClientBuilder;


    public boolean isValidToken(String token, String userName) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtToken)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String tokenUserId = claims.getSubject();

        return userName.equals(tokenUserId);
    }

    public void isAdd(Long id, Long target, boolean isAdd) {
        List<ServiceInstance> instances = discoveryClient.getInstances("rating-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("Rating service not found");
        } else {
            String url = (instances.get(0)).getUri().toString() + "/rating/isAddToRating";
            Boolean response = (webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header("X-User-OwnID", String.valueOf(id)))
                    .header("X-Target-Id", new String[]{String.valueOf(target)})
                    .header("X-Is-Add", String.valueOf(isAdd))
                    .retrieve().bodyToMono(Boolean.class).block();
        }
    }
}
