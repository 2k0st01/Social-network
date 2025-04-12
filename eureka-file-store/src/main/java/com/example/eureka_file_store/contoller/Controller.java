package com.example.eureka_file_store.contoller;

import com.example.eureka_file_store.config.DataProcessor;
import com.example.eureka_file_store.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class Controller {
    private final S3Service s3Service;
    private final DataProcessor dataProcessor;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @RequestParam("file") List<MultipartFile> file) {

        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(Collections.singletonList("Not authorized person."));
        }
        try {
            List<String> fileUrl = s3Service.uploadFiles(file, userId);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonList("Error uploading file: " + e.getMessage()));
        }
    }

    @PostMapping("/uploadTemp")
    public ResponseEntity<List<String>> uploadFileTemp(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @RequestParam("file") List<MultipartFile> file) {

        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(Collections.singletonList("Not authorized person."));
        }
        try {
            List<String> fileUrl = s3Service.uploadFiles(file, userId);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonList("Error uploading file: " + e.getMessage()));
        }
    }

    @PostMapping("/uploadUserPic")
    public ResponseEntity<String> uploadUserPic(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email,
            @RequestParam("file") MultipartFile file) {

        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body("Not authorized person.");
        }
        try {
            String fileUrl = s3Service.uploadAvatarFile(file, userId);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/api/photos/{id}")
    public List<String> listPhotos(@PathVariable Long id) {
        return s3Service.listAllPhotos(id);
    }

    @GetMapping("/api/photos")
    public ResponseEntity<List<String>> listPhotos(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Email") String email) {

        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.status(401).body(Collections.singletonList("Not authorized person."));
        }
        return ResponseEntity.ok(s3Service.listAllPhotos(userId));
    }
}
