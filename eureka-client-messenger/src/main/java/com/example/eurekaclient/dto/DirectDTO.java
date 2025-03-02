package com.example.eurekaclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DirectDTO implements Serializable {
    private Long id;
    private String userId;
    private String userName;
    private String lastMassage;
    private LocalDateTime timeLastMassage;

    public DirectDTO() {
    }

    public DirectDTO(Long id, String lastMassage, LocalDateTime timeLastMassage, String userId, String userName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.lastMassage = lastMassage;
        this.timeLastMassage = timeLastMassage;
    }
}
