package com.example.post.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
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

    public Map<Long,String> getUsersNames(Set<Long> list) {
        List<ServiceInstance> instances = discoveryClient.getInstances("auth-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("Auth service not found");
        }

        String url = instances.get(0).getUri().toString() + "/api/getUserNames";


        return webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(list)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,String>>() {})
                .block();
    }

    public Map<Long,String> getUsersAvatars(Set<Long> list) {
        List<ServiceInstance> instances = discoveryClient.getInstances("post-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("Post service not found");
        }

        String url = instances.get(0).getUri().toString() + "/post/getUserAvatars";

        return webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(list)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,String>>() {})
                .block();
    }
}
