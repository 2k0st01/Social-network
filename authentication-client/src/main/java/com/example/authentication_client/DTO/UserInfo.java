package com.example.authentication_client.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserInfo implements Serializable {
    private String email;
    private String username;
    private Long id;

    public UserInfo() {
    }

    public UserInfo(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
