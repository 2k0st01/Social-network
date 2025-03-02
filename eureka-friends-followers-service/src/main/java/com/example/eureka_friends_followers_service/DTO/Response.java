package com.example.eureka_friends_followers_service.DTO;

import lombok.Data;

@Data
public class Response {
    private Long id;
    private String username;

    public Response(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
