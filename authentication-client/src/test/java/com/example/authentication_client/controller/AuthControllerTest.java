package com.example.authentication_client.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

import com.example.authentication_client.DTO.EmailDTO;
import com.example.authentication_client.DTO.UserAccountDTO;
import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.service.ChangePasswordRequestTokenService;
import com.example.authentication_client.service.EmailConfirmationTokenService;
import com.example.authentication_client.service.JwtService;
import com.example.authentication_client.service.UserAccountService;
import com.example.authentication_client.utils.AuntResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private EmailConfirmationTokenService emailConfirmationTokenService;

    @MockBean
    private ChangePasswordRequestTokenService changePasswordRequestTokenService;

    @Test
    void testRegistrationNewUserAccount_Success() throws Exception {
        UserAccountDTO request = new UserAccountDTO("test@email.com", "testUser", "password123", "password123");
        when(userAccountService.registration(any())).thenReturn(true);

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void testRegistrationNewUserAccount_UserAlreadyExists() throws Exception {
        UserAccountDTO request = new UserAccountDTO("test@email.com", "testUser", "password123", "password123");
        when(userAccountService.registration(any())).thenReturn(false);

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already exist"));
    }

    @Test
    void testAuthUserAccount_Success() throws Exception {
        UserAccountDTO request = new UserAccountDTO("test@email.com", "", "password123", "password123");
        AuntResponse response = new AuntResponse("mocked-jwt-token", 1L, "testUser", "test@email.com");

        when(userAccountService.authenticate(any())).thenReturn(response);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void testAuthUserAccount_UserNotFound() throws Exception {
        UserAccountDTO request = new UserAccountDTO("test@email.com", "", "password123", "password123");

        when(userAccountService.authenticate(any())).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.token").value("Error: User not found"));
    }

    @Test
    void testValidateToken_Success() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test@email.com");
        userAccount.setUsername("testUser");

        when(jwtService.extractUsername("mocked-token")).thenReturn("test@email.com");
        when(userAccountService.loadUserByUsername("test@email.com")).thenReturn(userAccount);
        when(jwtService.isTokenValid("mocked-token", userAccount)).thenReturn(true);

        mockMvc.perform(post("/api/validateToken")
                        .header("Authorization", "Bearer mocked-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void testValidateToken_InvalidToken() throws Exception {
        mockMvc.perform(post("/api/validateToken")
                        .header("Authorization", "InvalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testVerificationEmail_Success() throws Exception {
        EmailDTO request = new EmailDTO("test@email.com");

        when(userAccountService.findUserAccountByEmail(request.getEmail()))
                .thenReturn(Optional.of(new UserAccount()));

        doAnswer(invocation -> null)
                .when(emailConfirmationTokenService)
                .requestForEmailConfirmation("test@email.com");


        mockMvc.perform(post("/api/verificationEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email was sent"));
    }

    @Test
    void testVerificationEmail_UserNotFound() throws Exception {
        EmailDTO request = new EmailDTO("notfound@email.com");

        when(userAccountService.findUserAccountByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/verificationEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User wasn't found with email: notfound@email.com"));
    }


}

