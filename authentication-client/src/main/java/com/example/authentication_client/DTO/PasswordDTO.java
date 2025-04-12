package com.example.authentication_client.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordDTO implements Serializable {
    private String lastPassword;
    private String newPassword;
    private String repeatNewPassword;

    public PasswordDTO(String lastPassword, String newPassword, String repeatNewPassword) {
        this.lastPassword = lastPassword;
        this.newPassword = newPassword;
        this.repeatNewPassword = repeatNewPassword;
    }
}
