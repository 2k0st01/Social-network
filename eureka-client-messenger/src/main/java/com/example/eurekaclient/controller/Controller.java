package com.example.eurekaclient.controller;

import com.example.eurekaclient.config.DataProcessor;
import com.example.eurekaclient.direct.Direct;
import com.example.eurekaclient.direct.DirectService;
import com.example.eurekaclient.dto.DirectDTO;
import com.example.eurekaclient.dto.MessageDTO;
import com.example.eurekaclient.dto.MessagesDTO;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis")
@AllArgsConstructor
public class Controller {
    private final DirectService directService;
    private final DataProcessor dataProcessor;

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
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
                                      @RequestBody MessageDTO message) {
        if (!dataProcessor.isValidToken(token, email)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return directService.send(userName, userId, message.getToUserId(), message.getMessage());
    }

    @GetMapping("/getMessages/{id}")
    public List<MessagesDTO> getMessages(@RequestHeader(value="Authorization") String token,
                                         @RequestHeader(value="X-User-Id") String userId,
                                         @RequestHeader(value="X-User-Name") String username,
                                         @RequestHeader(value="X-User-Email") String email,
                                         @PathVariable String id,
                                         @RequestParam(value="page", defaultValue="0") Integer page) {
        if (!dataProcessor.isValidToken(token, email)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(page == 0){
            return directService.getMessages(id,username,userId);
        } else
            return directService.getMessages(id, username, userId, page);
    }

}
