package com.example.post.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String userAvatar;
    private String username;

    public UserDTO() {
    }

    public UserDTO(Long userId, String userAvatar, String username) {
        this.userId = userId;
        this.userAvatar = userAvatar;
        this.username = username;
    }

}
