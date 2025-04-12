package com.example.search_history.controller;

import com.example.search_history.DTO.RequestForSearch;
import com.example.search_history.config.DataProcessor;
import com.example.search_history.search.SearchService;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/api")
@AllArgsConstructor
public class Controller {

    private final DataProcessor dataProcessor;
    private final SearchService service;


    @PostMapping("/save")
    public void saveUserSearchData(@RequestHeader(value="Authorization") String token,
                                   @RequestHeader(value="X-User-Id") String userId,
                                   @RequestHeader(value="X-User-Name") String userName,
                                   @RequestHeader(value="X-User-Email") String email,
                                   @RequestBody RequestForSearch request) {
        if (!dataProcessor.isValidToken(token, email)) {
            return;
        }
        service.saveUserSearchData(userId, request);
    }

    @GetMapping("/get")
    public List<RequestForSearch> getUserSearchData(@RequestHeader(value="Authorization") String token,
                                                    @RequestHeader(value="X-User-Id") String userId,
                                                    @RequestHeader(value="X-User-Name") String userName,
                                                    @RequestHeader(value="X-User-Email") String email) {
        if (!dataProcessor.isValidToken(token, email)) {
            return null;
        }
        return service.getUserSearchData(userId);
    }
}
