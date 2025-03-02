package com.example.authentication_client.controller;

import com.example.authentication_client.DTO.PasswordDTO;
import com.example.authentication_client.DTO.response.ApiResponse;
import com.example.authentication_client.service.JwtService;
import com.example.authentication_client.service.UserAccountService;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/date")
@AllArgsConstructor
public class AccountRequestController {
    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse> changePassword(@RequestHeader(value="Authorization") String token, @RequestBody PasswordDTO passwordDTO) {
        try {
            token = token.substring(7);
            String email = jwtService.extractUsername(token);
            userAccountService.changerPassword(passwordDTO, email, token);
            return ResponseEntity.badRequest().body(new ApiResponse(true, "Password was changed"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error:: " + e.getMessage()));
        }
    }
}
