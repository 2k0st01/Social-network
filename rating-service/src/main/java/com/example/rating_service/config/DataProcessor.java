package com.example.rating_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DataProcessor {
    @Value("${jwt.secret}")
    private String jwtToken;

    public boolean isValidToken(String token, String userName) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtToken)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return userName.equals(claims.getSubject());
    }


}
