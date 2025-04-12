package com.example.eurekaclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MessageDTO implements Serializable {
    private String toUserId;
    private String message;

    public MessageDTO() {

    }
}
