package com.example.post.controller;

import com.example.post.DTO.Response;
import com.example.post.DTO.UserDTO;
import com.example.post.config.DataProcessor;
import com.example.post.model.FFService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<UserDTO>> getFollowing(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ffService.getAllFollowing(id,page));
    }

    @GetMapping("/getFollowers/{id}")
    public ResponseEntity<List<UserDTO>> getFollowers(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ffService.getAllFollowers(id,page));
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

    @GetMapping("/top10Users")
    public List<UserDTO> findTop10ByFollowers(){
        return ffService.findTop10ByFollowers();
    }
}
