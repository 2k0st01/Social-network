package com.example.post.controller;

import com.example.post.DTO.UserDTO;
import com.example.post.config.DataProcessor;
import com.example.post.model.FFService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FFService ffService;

    @MockBean
    private DataProcessor dataProcessor;

    private final String authToken = "Bearer test-token";
    private final Long userId = 1L;
    private final String userName = "testUser";
    private final String email = "test@example.com";

    @Test
    void shouldReturnFollowingCount() throws Exception {
        Long targetId = 2L;
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.getFollowingCount(targetId)).thenReturn(5);

        mockMvc.perform(get("/ff/getFollowingCount/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void shouldReturnFollowersCount() throws Exception {
        Long targetId = 2L;
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.getFollowersCount(targetId)).thenReturn(10);

        mockMvc.perform(get("/ff/getFollowersCount/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void shouldReturnFollowingList() throws Exception {
        Long targetId = 2L;
        List<UserDTO> following = List.of(
                new UserDTO(3L, "url", "user3"),
                new UserDTO(4L, "url", "user4"),
                new UserDTO(5L, "url", "user5"));

        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.getAllFollowing(targetId, 0)).thenReturn(following);

        mockMvc.perform(get("/ff/getFollowing/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                [
                    {"userId":3, "userAvatar":"url", "username":"user3"},
                    {"userId":4, "userAvatar":"url", "username":"user4"},
                    {"userId":5, "userAvatar":"url", "username":"user5"}
                ]
                """));
    }


    @Test
    void shouldReturnFollowersList() throws Exception {
        Long targetId = 2L;
        List<UserDTO> followers = List.of(
                new UserDTO(6L,"url", "user6"),
                new UserDTO(7L,"url", "user7"),
                new UserDTO(8L,"url", "user8"));

        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.getAllFollowers(targetId,0)).thenReturn(followers);

        mockMvc.perform(get("/ff/getFollowers/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {"userId":6, "userAvatar":"url", "username":"user6"},
                        {"userId":7, "userAvatar":"url", "username":"user7"},
                        {"userId":8, "userAvatar":"url", "username":"user8"}
                    ]
                    """));
    }

    @Test
    void shouldReturnUserName() throws Exception {
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);

        mockMvc.perform(get("/ff/getUserName")
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

    }

    @Test
    void shouldCreateFF() throws Exception {
        Long targetId = 2L;
        when(ffService.create(targetId)).thenReturn(true);

        mockMvc.perform(post("/ff/create/{id}", targetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldToggleFollow() throws Exception {
        Long targetId = 2L;
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.toggleFollow(targetId, userId)).thenReturn("Followed");

        mockMvc.perform(post("/ff/toggleFollow/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Followed"));
    }

    @Test
    void shouldCheckIfHasFollow() throws Exception {
        Long targetId = 2L;
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(true);
        when(ffService.hasFollow(targetId, userId)).thenReturn(true);

        mockMvc.perform(get("/ff/hasFollow/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenInvalid() throws Exception {
        Long targetId = 2L;
        when(dataProcessor.isValidToken(authToken, email)).thenReturn(false);

        mockMvc.perform(get("/ff/getFollowing/{id}", targetId)
                        .header("Authorization", authToken)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", userName)
                        .header("X-User-Email", email))
                .andExpect(status().isUnauthorized());
    }
}

