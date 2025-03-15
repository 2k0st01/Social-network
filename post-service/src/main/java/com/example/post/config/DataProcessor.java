package com.example.post.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class DataProcessor {
    @Value("${jwt.secret}")
    private String jwtSecret;

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
