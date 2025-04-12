package com.example.post.config;

import com.example.post.modelDTO.PostDTO;
import com.example.post.modelDTO.SortDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.security.Key;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataProcessor {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final DiscoveryClient discoveryClient;
    private final WebClient.Builder webClientBuilder;

    public List<SortDTO> sortUserIds(Long userID, int page) {
        List<ServiceInstance> instances = discoveryClient.getInstances("rating-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("Rating service not found");
        }

        String url = instances.get(0).getUri().toString() + "/rating/findByOwnIdSorted?page=" + page;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .header("X-User-Id", String.valueOf(userID))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SortDTO>>() {})
                .block();
    }

    public List<PostDTO> getPostsForFeedNews(List<SortDTO> list, Long userID, int page) {
        List<ServiceInstance> instances = discoveryClient.getInstances("post-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("Post service not found");
        }

        String url = instances.get(0).getUri().toString() + "/post/getPostsForFeedNews?page=" + page;

        return webClientBuilder.build()
                .post()
                .uri(url)
                .header("X-User-Id", String.valueOf(userID))
                .bodyValue(list)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostDTO>>() {})
                .block();
    }

    public boolean isValidToken(String token, String userName) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String tokenUserId = claims.getSubject();
        return userName.equals(tokenUserId);
    }
}
