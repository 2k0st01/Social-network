package com.example.post.controller;

import com.example.post.builder.Builder;
import com.example.post.config.DataProcessor;
import com.example.post.modelDTO.PostDTO;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@AllArgsConstructor
public class Controller {
    private final Builder builder;
    private final DataProcessor dataProcessor;

    @GetMapping(value={"/getFeed"})
    public List<PostDTO> getFeed(@RequestHeader(value="Authorization") String token,
                                 @RequestHeader(value="X-User-Id") Long userID,
                                 @RequestHeader(value="X-User-Email") String email,
                                 @RequestParam int page) {
        if (!dataProcessor.isValidToken(token, email)) {
            return null;
        }
        return builder.newsBuilder(userID, page);
    }
}
