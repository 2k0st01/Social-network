package com.example.eureka_friends_followers_service.controller;

import com.example.eureka_friends_followers_service.DTO.Response;
import com.example.eureka_friends_followers_service.config.DataProcessor;
import com.example.eureka_friends_followers_service.model.FFService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ff")
public class Controller {
    private final FFService ffService;
    private final DataProcessor dataProcessor;

    public Controller(FFService ffService, DataProcessor dataProcessor) {
        this.ffService = ffService;
        this.dataProcessor = dataProcessor;
    }

    @GetMapping("/getFollowingCount/{id}")
    public ResponseEntity<Integer> getFollowingCount(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable(name = "id") Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(0);
        }
        return ResponseEntity.ok(ffService.getFollowingCount(id));
    }

    @GetMapping("/getFollowersCount/{id}")
    public ResponseEntity<Integer> getFollowersCount(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(0);
        }
        return ResponseEntity.ok(ffService.getFollowersCount(id));
    }

    @GetMapping("/getFollowing/{id}")
    public ResponseEntity<Set<Long>> getFollowing(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ffService.getAllFollowing(id));
    }

    @GetMapping("/getFollowers/{id}")
    public ResponseEntity<Set<Long>> getFollowers(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ffService.getAllFollowers(id));
    }

    @GetMapping("/getUserName")
    public ResponseEntity<Response> getUserName(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new Response(userId, userName));
    }



    @PostMapping("/create/{id}")
    public ResponseEntity<Boolean> createFF(@PathVariable Long id) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ffService.create(id));
    }

    @PostMapping("/toggleFollow/{id}")
    public ResponseEntity<String> toggleFollow(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ffService.toggleFollow(id, userId));
    }

    @GetMapping("/hasFollow/{id}")
    public ResponseEntity<Boolean> hasFollow(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(false);
        }
        return ResponseEntity.ok(ffService.hasFollow(id, userId));
    }
}
