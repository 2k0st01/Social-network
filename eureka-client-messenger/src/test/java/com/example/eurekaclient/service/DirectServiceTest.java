package com.example.eurekaclient.service;

import com.example.eurekaclient.direct.DirectService;
import com.example.eurekaclient.dto.DirectDTO;
import com.example.eurekaclient.dto.MessagesDTO;
import com.example.eurekaclient.kafka.KafkaProducerService;
import com.example.eurekaclient.messenger.Messages;
import com.example.eurekaclient.messenger.MessagesRepository;
import com.example.eurekaclient.config.DataProcessor;
import com.example.eurekaclient.direct.Direct;
import com.example.eurekaclient.direct.DirectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class DirectServiceTest {

    @Mock
    private DirectRepository directRepository;

    @Mock
    private MessagesRepository messagesRepository;

    @Mock
    private DataProcessor dataProcessor;

    @Mock
    private KafkaProducerService kafkaProducerService;

    private DirectService directService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        directService = new DirectService(directRepository, messagesRepository, dataProcessor, kafkaProducerService);
    }

    @Test
    public void testCreateDirect_success() {
        String firstUserId = "user1";
        String secondUserId = "user2";

        when(dataProcessor.getUserNamesById(firstUserId, secondUserId)).thenReturn(List.of("User One", "User Two"));

        Optional<Direct> result = directService.createDirect(firstUserId, secondUserId);

        assertTrue(result.isPresent());
        assertEquals(firstUserId, result.get().getFirstUserId());
        assertEquals(secondUserId, result.get().getSecondUserId());
    }

    @Test
    public void testCreateDirect_invalidNames() {
        String firstUserId = "user1";
        String secondUserId = "user2";

        when(dataProcessor.getUserNamesById(firstUserId, secondUserId)).thenReturn(List.of("User One"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            directService.createDirect(firstUserId, secondUserId);
        });

        assertEquals("Expected at least 2 elements in the list, but got: 1", exception.getMessage());
    }

    @Test
    public void testSend_success() {
        String userName = "User1";
        String userId = "user1";
        String toUser = "user2";
        String message = "Hello!";

        when(dataProcessor.checkUserExists(toUser)).thenReturn(true);
        when(directRepository.findDirectByUserIds(userId, toUser)).thenReturn(Optional.of(new Direct()));

        boolean result = directService.send(userName, userId, toUser, message);

        assertTrue(result);
        verify(kafkaProducerService).incDecRating(userId, toUser, "MESSAGE");
    }

    @Test
    public void testSend_userNotFound() {
        String userName = "User1";
        String userId = "user1";
        String toUser = "user2";
        String message = "Hello!";

        when(dataProcessor.checkUserExists(toUser)).thenReturn(false);

        boolean result = directService.send(userName, userId, toUser, message);

        assertFalse(result);
    }

    @Test
    public void testGetDirects_success() {
        String userId = "user1";
        String username = "User1";
        int page = 0;

        Page<DirectDTO> pageResult = new PageImpl<>(List.of(new DirectDTO()));
        when(directRepository.getAllDirectsByUserNameAsDTO(userId, username, PageRequest.of(page, 25, Sort.by("lastTimeUpdate").descending())))
                .thenReturn(pageResult);

        List<DirectDTO> result = directService.getDirects(userId, username, page);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetDirects_notFound() {
        String userId = "user1";
        String username = "User1";
        int page = 0;

        when(directRepository.getAllDirectsByUserNameAsDTO(userId, username, PageRequest.of(page, 25, Sort.by("lastTimeUpdate").descending())))
                .thenReturn(Page.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            directService.getDirects(userId, username, page);
        });

        assertEquals("Direct wasn't found.", exception.getMessage());
    }

    @Test
    public void testGetMessages_success() {
        String firstID = "user1";
        String userName = "User1";
        String secondID = "user2";
        int page = 0;

        Direct direct = new Direct();
        direct.setId(1L);
        direct.setFirstUserId(firstID);
        direct.setSecondUserId(secondID);

        Page<MessagesDTO> pageResult = new PageImpl<>(List.of(new MessagesDTO()));
        when(directRepository.findDirectByUserIds(firstID, secondID)).thenReturn(Optional.of(direct));
        when(messagesRepository.getMessageDTObyDirectId(direct.getId(), userName, PageRequest.of(page, 25, Sort.by("time").descending())))
                .thenReturn(pageResult);

        List<MessagesDTO> result = directService.getMessages(firstID, userName, secondID, page);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    public void testGetMessages_directNotFound() {
        String firstID = "user1";
        String userName = "User1";
        String secondID = "user2";
        int page = 0;

        when(directRepository.findDirectByUserIds(firstID, secondID)).thenReturn(Optional.empty());

        List<MessagesDTO> result = directService.getMessages(firstID, userName, secondID, page);

        assertTrue(result.isEmpty());
    }


}

