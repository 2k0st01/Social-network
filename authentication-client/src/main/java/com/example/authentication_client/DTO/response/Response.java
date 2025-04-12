package com.example.authentication_client.DTO.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private Long id;
    private String username;

    public Response() {
    }

    public Response(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
