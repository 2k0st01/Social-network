package com.example.api_gateway.filter;

import com.example.api_gateway.dto.UserInfo;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    public JwtAuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        List<String> list = new ArrayList<>();

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String jwt = extractToken(request, config);

            if (jwt == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return authenticateUser(jwt, config, exchange, chain);
        };
    }

    private String extractToken(ServerHttpRequest request, Config config) {
        String authHeader;

        if (request.getURI().getPath().startsWith("/ws")) {
            authHeader = request.getQueryParams().getFirst(config.getAuthHeader());
        } else {
            authHeader = request.getHeaders().getFirst(config.getAuthHeader());
        }

        if (authHeader != null && authHeader.startsWith(config.getTokenPrefix())) {
            return authHeader.substring(config.getTokenPrefix().length());
        }

        return null;
    }

    private Mono<Void> authenticateUser(String jwt, Config config, ServerWebExchange exchange, GatewayFilterChain chain) {
        return webClientBuilder.build()
                .post()
                .uri(config.getAuthServiceUri())
                .header(config.getAuthHeader(), config.getTokenPrefix() + jwt)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .flatMap(userInfo -> {
                    if (userInfo != null) {
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", String.valueOf(userInfo.getId()))
                                .header("X-User-name", userInfo.getUsername())
                                .header("X-User-email", userInfo.getEmail())
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }


    public static class Config {
        private String authServiceUri = "lb://AUTH-SERVICE/api/validateToken";
        private String authHeader = "Authorization";
        private String tokenPrefix = "Bearer ";

        public String getAuthServiceUri() {
            return authServiceUri;
        }

        public void setAuthServiceUri(String authServiceUri) {
            this.authServiceUri = authServiceUri;
        }

        public String getAuthHeader() {
            return authHeader;
        }

        public void setAuthHeader(String authHeader) {
            this.authHeader = authHeader;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }
    }


}
