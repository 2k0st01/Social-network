package com.example.eurekaclient.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessengerRequest implements Serializable {
    private String getMessengerUserId;
    private String messenger;


}
