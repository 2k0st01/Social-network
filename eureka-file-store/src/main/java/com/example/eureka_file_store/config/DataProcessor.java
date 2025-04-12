package com.example.eureka_file_store.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataProcessor {
    @Value(value="${jwt.secret}")
    private String jwtToken;

    public boolean isValidToken(String token, String userName){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtToken)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String tokenUserId = claims.getSubject();

        return userName.equals(tokenUserId);
    }


}
