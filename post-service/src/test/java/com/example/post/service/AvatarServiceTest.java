package com.example.post.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.post.models.avatar.AvatarRepository;
import com.example.post.models.avatar.AvatarService;
import com.example.post.models.avatar.UserAvatar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    private AvatarRepository avatarRepository;

    @InjectMocks
    private AvatarService userAvatarService;

    private final Long userId = 1L;
    private final String avatarUrl = "https://example.com/avatar.jpg";

    @BeforeEach
    void setUp() {
        reset(avatarRepository);
    }

    @Test
    void testCreateAvatarForUser_WhenAvatarExists() {
        UserAvatar existingAvatar = new UserAvatar(userId, "https://old-url.com/avatar.jpg");
        when(avatarRepository.findUserAvatarByOwnId(userId)).thenReturn(Optional.of(existingAvatar));

        boolean result = userAvatarService.creatAvatarForUser(avatarUrl, userId);

        assertTrue(result);
        assertEquals(avatarUrl, existingAvatar.getUrl());
        verify(avatarRepository, never()).save(any(UserAvatar.class));
    }

    @Test
    void testCreateAvatarForUser_WhenAvatarDoesNotExist() {
        when(avatarRepository.findUserAvatarByOwnId(userId)).thenReturn(Optional.empty());

        boolean result = userAvatarService.creatAvatarForUser(avatarUrl, userId);

        assertTrue(result);
        verify(avatarRepository).save(any(UserAvatar.class));
    }

    @Test
    void testGetAvatarUrlByOwnId_WhenAvatarExists() {
        when(avatarRepository.getUserAvatarByOwnId(userId)).thenReturn(Optional.of(new UserAvatar(userId, avatarUrl)));

        String result = userAvatarService.getAvatarUrlByOwnId(userId);

        assertEquals(avatarUrl, result);
    }

    @Test
    void testGetAvatarUrlByOwnId_WhenAvatarDoesNotExist() {
        when(avatarRepository.getUserAvatarByOwnId(userId)).thenReturn(Optional.empty());

        String result = userAvatarService.getAvatarUrlByOwnId(userId);

        assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4g_2Qj3LsNR-iqUAFm6ut2EQVcaou4u2YXw&s", result);
    }
}

