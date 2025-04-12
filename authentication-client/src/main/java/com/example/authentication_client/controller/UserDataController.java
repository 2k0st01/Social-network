package com.example.authentication_client.controller;

import com.example.authentication_client.DTO.UserInfo;
import com.example.authentication_client.DTO.UserRequestDTO;
import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.service.JwtService;
import com.example.authentication_client.service.UserAccountService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scan")
@AllArgsConstructor
public class UserDataController {
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @GetMapping("/users")
    public List<UserInfo> searchUsersByUsername(@RequestHeader(value="Authorization") String token, @RequestParam(name = "username") String username) {
        token = token.substring(7);
        String email = jwtService.extractUsername(token);
        return userAccountService.findUserAccountByUserName(username, token, email);
    }

    @PostMapping("/getUserName")
    public UserInfo getUserName(@RequestBody UserRequestDTO id) {
        Optional<UserAccount> userAccount = userAccountService.findUserAccountById(id.getId());
        return userAccount.map((account) -> new UserInfo(account.getId(), account.getUsernameByUser(), account.getEmail())).orElse(null);
    }




}
