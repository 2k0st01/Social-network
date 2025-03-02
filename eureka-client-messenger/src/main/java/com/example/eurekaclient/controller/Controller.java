package com.example.eurekaclient.controller;

import com.example.eurekaclient.config.DataProcessor;
import com.example.eurekaclient.direct.Direct;
import com.example.eurekaclient.direct.DirectService;
import com.example.eurekaclient.dto.DirectDTO;
import com.example.eurekaclient.dto.MessagesDTO;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis")
@AllArgsConstructor
public class Controller {
    private final DirectService directService;
    private final DataProcessor dataProcessor;

    @GetMapping("/v1")
    public ResponseEntity<String> getUserInfo(@RequestHeader(value="Authorization") String token,
                                              @RequestHeader(value="X-User-Id") String userId,
                                              @RequestHeader(value="X-User-Name") String userName) {
        if (!dataProcessor.isValidToken(token, userName)) {
            return ResponseEntity.ok("Invalid request");
        }
        String userInfo = "User ID: " + userId + ", Username: " + userName;
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/createDirect")
    public boolean createDirects(@RequestHeader(value="X-User-Id") String userId,
                                 @RequestHeader(value="X-User-Name") String userName,
                                 @RequestParam String secondUserName) {
        Optional<Direct> direct = directService.createDirect(userName, secondUserName);
        return direct.isPresent();
    }

    @GetMapping("/direct")
    public List<DirectDTO> getDirectsByUsername(@RequestHeader(value="Authorization") String token,
                                                @RequestHeader(value="X-User-Id") String userId,
                                                @RequestHeader(value="X-User-Name") String userName,
                                                @RequestHeader(value="X-User-Email") String email,
                                                @RequestParam(value="page", defaultValue="0") Integer page) {
        if (!dataProcessor.isValidToken(token, email)) {
            return null;
        }
        try {
            return directService.getDirects(userId, userName, page);
        }
        catch (IllegalStateException e) {
            return null;
        }
    }

    @PostMapping("/send")
    public boolean getDirectsByUserId(@RequestHeader(value="Authorization") String token,
                                      @RequestHeader(value="X-User-Id") String userId,
                                      @RequestHeader(value="X-User-Name") String userName,
                                      @RequestHeader(value="X-User-Email") String email,
                                      @RequestParam String toUserId, String message) {
        if (!dataProcessor.isValidToken(token, email)) {
            return false;
        }
        return directService.send(userName, userId, toUserId, message);
    }

    @GetMapping("/getMessages/{id}")
    public List<MessagesDTO> getMessages(@RequestHeader(value="Authorization") String token,
                                         @RequestHeader(value="X-User-Id") String userId,
                                         @RequestHeader(value="X-User-Name") String userName,
                                         @RequestHeader(value="X-User-Email") String email,
                                         @PathVariable String id,
                                         @RequestParam(value="page", defaultValue="0") Integer page) {
        if (!dataProcessor.isValidToken(token, email)) {
            return null;
        }
        return directService.getMessages(id, userName, userId, page);
    }

}
