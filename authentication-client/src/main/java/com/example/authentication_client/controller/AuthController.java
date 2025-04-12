package com.example.authentication_client.controller;

import com.example.authentication_client.DTO.EmailDTO;
import com.example.authentication_client.DTO.PasswordDTO;
import com.example.authentication_client.DTO.UserAccountDTO;
import com.example.authentication_client.DTO.UserInfo;
import com.example.authentication_client.DTO.response.ApiResponse;
import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.model.token.ChangePasswordRequestToken;
import com.example.authentication_client.service.ChangePasswordRequestTokenService;
import com.example.authentication_client.service.EmailConfirmationTokenService;
import com.example.authentication_client.service.JwtService;
import com.example.authentication_client.service.UserAccountService;
import com.example.authentication_client.utils.AuntResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private final UserAccountService userAccountService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final ChangePasswordRequestTokenService changePasswordRequestTokenService;
    private final JwtService jwtService;

    @GetMapping("/checkUserExist/{userId}")
    public boolean findUser(@PathVariable Long userId) {
        return userAccountService.existsUserAccountById(userId);
    }

    @GetMapping("/getUsersNames")
    public List<String> getUsersNames(@RequestParam String firstId, @RequestParam String secondId) {
        return userAccountService.findUserNamesById(firstId, secondId);
    }

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse> registrationNewUserAccount(@RequestBody @Valid UserAccountDTO request) {
        boolean response = userAccountService.registration(request);
        return ResponseEntity.ok(new ApiResponse(response, response ? "Registration successful" : "User already exist"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuntResponse> authUserAccount(@RequestBody UserAccountDTO request) {
        System.out.println(request.getEmail() + " : " + request.getUsername());
        try {
            return ResponseEntity.ok(userAccountService.authenticate(request));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new AuntResponse("Error: " + e.getMessage(), "", null, null));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new AuntResponse("Error: Login or password not correct.", "", null, null));
        }
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserInfo> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        token = token.substring(7);
        String username = jwtService.extractUsername(token);
        if (username != null) {
            UserAccount account = userAccountService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, account)) {
                UserInfo userInfo = new UserInfo(account.getId(), account.getUsernameByUser(), account.getEmail());
                return ResponseEntity.ok(userInfo);
            }
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/verificationEmail")
    public ResponseEntity<ApiResponse> verificationEmail(@RequestBody EmailDTO request) {
        try {
            Optional<UserAccount> user = userAccountService.findUserAccountByEmail(request.getEmail());
            if (user.isPresent()) {
                emailConfirmationTokenService.requestForEmailConfirmation(request.getEmail());
                return ResponseEntity.ok(new ApiResponse(true, "Email was sent"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse(false, "User wasn't found with email: " + request.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public String checkTokenValidation(@RequestParam String token) {
        return emailConfirmationTokenService.confirmToken(token);
    }

    @PostMapping("/recoverPassword")
    public ResponseEntity<ApiResponse> requestForRecoverPassword(@RequestBody EmailDTO request) {
        try {
            Optional<UserAccount> user = userAccountService.findUserAccountByEmail(request.getEmail());
            if (user.isPresent()) {
                changePasswordRequestTokenService.requestForRecoverPassword(request.getEmail());
                return ResponseEntity.ok(new ApiResponse(true, "Email was sent"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse(false, "User wasn't found with email: " + request.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/getUserNames")
    public Map<Long, String> getUserNames(@RequestBody Set<Long> id) {
        System.out.println("-----I'm here=-----");
        return  userAccountService.findUserAccountsByIds(id);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse> recoverPassword(@RequestParam String token, @RequestBody PasswordDTO passwordDTO) {
        try {
            ChangePasswordRequestToken changePasswordRequestToken = changePasswordRequestTokenService.findChangePasswordRequestTokenByToken(token);
            if (changePasswordRequestToken != null) {
                UserAccount userAccount = changePasswordRequestToken.getUserAccount();
                boolean response = userAccountService.recoverPassword(passwordDTO, userAccount);
                return response
                        ? ResponseEntity.ok(new ApiResponse(true, "Password was changed"))
                        : ResponseEntity.badRequest().body(new ApiResponse(false, "Passwords do not match"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Token not valid"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: " + e.getMessage()));
        }
    }
}
