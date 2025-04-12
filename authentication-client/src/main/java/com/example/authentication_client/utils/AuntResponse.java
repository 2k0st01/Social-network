package com.example.authentication_client.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class AuntResponse {
    private String token;
    private String username;
    private String email;
    private Long userId;

    public AuntResponse(String token, Long userId, String username, String email ) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }
}
