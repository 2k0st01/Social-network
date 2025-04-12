package com.example.post.service;

import com.example.post.config.DataProcessor;
import com.example.post.model.FFRepository;
import com.example.post.model.FFService;
import com.example.post.model.FollowersFollowing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FFServiceTest {

    @Mock
    private FFRepository repository;

    @Mock
    private DataProcessor dataProcessor;

    @InjectMocks
    private FFService service;

    @Test
    void shouldSaveEntityAndReturnTrue() {
        Long id = 1L;

        boolean result = service.create(id);

        assertTrue(result);
        verify(repository, times(1)).save(argThat(f -> f.getId().equals(id)));
    }

    @Test
    void shouldThrowExceptionIfSaveFails() {
        Long id = 2L;
        doThrow(new RuntimeException("DB error")).when(repository).save(any());

        assertThrows(RuntimeException.class, () -> service.create(id));
    }

    @Test
    void shouldUnfollowIfAlreadyFollowing() {
        Long targetID = 1L;
        Long ownID = 2L;

        FollowersFollowing targetUser = new FollowersFollowing();
        targetUser.setId(targetID);
        targetUser.addToFollowers(ownID);

        FollowersFollowing ownUser = new FollowersFollowing();
        ownUser.setId(ownID);
        ownUser.addToFollowing(targetID);

        when(repository.findById(targetID)).thenReturn(Optional.of(targetUser));
        when(repository.findById(ownID)).thenReturn(Optional.of(ownUser));
        when(repository.existsFollower(ownID, targetID)).thenReturn(true); // Додано мок!

        String result = service.toggleFollow(targetID, ownID);

        assertEquals("Unfollowed", result);

        assertFalse(targetUser.getFollowers().stream().anyMatch(f -> f.getFollowerId().equals(ownID)));
        assertFalse(ownUser.getFollowing().stream().anyMatch(f -> f.getFollowingId().equals(targetID)));

        verify(dataProcessor).isAdd(ownID, targetID, false);
        verify(repository, times(1)).saveAll(any());
    }


    @Test
    void shouldFollowIfNotFollowing() {
        Long targetID = 1L;
        Long ownID = 2L;

        FollowersFollowing targetUser = new FollowersFollowing();
        targetUser.setId(targetID);

        FollowersFollowing ownUser = new FollowersFollowing();
        ownUser.setId(ownID);

        when(repository.findById(targetID)).thenReturn(Optional.of(targetUser));
        when(repository.findById(ownID)).thenReturn(Optional.of(ownUser));
        when(repository.existsFollower(ownID, targetID)).thenReturn(false); // Додано мок!

        String result = service.toggleFollow(targetID, ownID);

        assertEquals("Followed", result);

        assertTrue(targetUser.getFollowers().stream().anyMatch(f -> f.getFollowerId().equals(ownID)));
        assertTrue(ownUser.getFollowing().stream().anyMatch(f -> f.getFollowingId().equals(targetID)));

        verify(dataProcessor).isAdd(ownID, targetID, true);
        verify(repository, times(1)).saveAll(any());
    }


    @Test
    void shouldThrowExceptionIfTargetUserNotFound() {
        Long targetID = 1L;
        Long ownID = 2L;

        when(repository.findById(targetID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.toggleFollow(targetID, ownID));

        verify(repository, never()).save(any());
        verify(dataProcessor, never()).isAdd(any(), any(), anyBoolean());
    }

    @Test
    void shouldThrowExceptionIfOwnUserNotFound() {
        Long targetID = 1L;
        Long ownID = 2L;

        FollowersFollowing targetUser = new FollowersFollowing();
        targetUser.setId(targetID);

        when(repository.findById(targetID)).thenReturn(Optional.of(targetUser));
        when(repository.findById(ownID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.toggleFollow(targetID, ownID));

        verify(repository, never()).save(any());
        verify(dataProcessor, never()).isAdd(any(), any(), anyBoolean());
    }

    @Test
    void shouldReturnTrueIfUserIsFollowing() {
        Long id = 1L;
        Long userId = 2L;

        when(repository.existsFollower(userId, id)).thenReturn(true);

        boolean result = service.hasFollow(id, userId);

        assertTrue(result);

        verify(repository).existsFollower(userId, id);
    }


    @Test
    void shouldReturnFalseIfUserIsNotFollowing() {
        Long id = 1L;
        Long userId = 2L;

        boolean result = service.hasFollow(id, userId);

        assertFalse(result);
    }


    @Test
    void shouldReturnFalseIfUserDoesNotExist() {
        Long id = 1L;
        Long userId = 2L;

        boolean result = service.hasFollow(id, userId);

        assertFalse(result);
    }



}
