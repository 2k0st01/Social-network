package com.example.authentication_client.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserAccountDTO implements Serializable {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Repeat password cannot be empty")
    private String doublePassword;


    public UserAccountDTO() {
    }

    public UserAccountDTO(String email, String username, String password, String doublePassword) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.doublePassword = doublePassword;
    }


}
