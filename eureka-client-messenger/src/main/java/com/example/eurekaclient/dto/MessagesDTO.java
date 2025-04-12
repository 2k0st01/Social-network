package com.example.eurekaclient.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MessagesDTO implements Serializable {
    private String userName;
    private Integer user;
    private Long userId;
    private String message;
    private LocalDateTime time;

    public MessagesDTO() {
    }

    public MessagesDTO(String userName, String message, Long userId, LocalDateTime time, Integer user) {
        this.userName = userName;
        this.user = user;
        this.userId = userId;
        this.message = message;
        this.time = time;
    }

}
