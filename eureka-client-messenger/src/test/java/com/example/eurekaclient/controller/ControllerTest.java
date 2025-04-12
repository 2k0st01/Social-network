package com.example.eurekaclient.controller;

import com.example.eurekaclient.config.DataProcessor;
import com.example.eurekaclient.controller.Controller;
import com.example.eurekaclient.direct.Direct;
import com.example.eurekaclient.direct.DirectService;
import com.example.eurekaclient.dto.DirectDTO;
import com.example.eurekaclient.dto.MessageDTO;
import com.example.eurekaclient.dto.MessagesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(Controller.class)
public class ControllerTest {

    @MockBean
    private DirectService directService;

    @MockBean
    private DataProcessor dataProcessor;

    private Controller controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new Controller(directService,dataProcessor);
    }

    @Test
    public void testCreateDirect() {
        String userId = "user1";
        String userName = "User1";
        String secondUserName = "User2";

        when(directService.createDirect(userName, secondUserName)).thenReturn(Optional.of(new Direct()));

        boolean result = controller.createDirects(userId, userName, secondUserName);

        assertTrue(result);

        verify(directService, times(1)).createDirect(userName, secondUserName);
    }




    @Test
    public void testGetDirects_validToken() {
        String token = "valid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        int page = 0;

        List<DirectDTO> directs = List.of(new DirectDTO());
        when(dataProcessor.isValidToken(token, email)).thenReturn(true);
        when(directService.getDirects(userId, userName, page)).thenReturn(directs);

        List<DirectDTO> result = controller.getDirectsByUsername(token, userId, userName, email, page);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(directService, times(1)).getDirects(userId, userName, page);
    }

    @Test
    public void testGetDirects_invalidToken() {
        String token = "invalid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        int page = 0;

        when(dataProcessor.isValidToken(token, email)).thenReturn(false);

        List<DirectDTO> result = controller.getDirectsByUsername(token, userId, userName, email, page);

        assertTrue(result.isEmpty());
    }

    // Тест для /apis/send
    @Test
    public void testSendMessage_validToken() {
        String token = "valid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        String toUserId = "user2";
        String message = "Hello";
        MessageDTO dto = new MessageDTO();
        dto.setMessage(message);
        dto.setToUserId(toUserId);

        when(dataProcessor.isValidToken(token, email)).thenReturn(true);
        when(directService.send(userName, userId, toUserId, message)).thenReturn(true);

        boolean result = controller.getDirectsByUserId(token, userId, userName, email, dto);

        assertTrue(result);
        verify(directService, times(1)).send(userName, userId, toUserId, message);
    }

    @Test
    public void testSendMessage_invalidToken() {
        String token = "invalid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        String toUserId = "user2";
        String message = "Hello";
        MessageDTO dto = new MessageDTO();
        dto.setMessage(message);
        dto.setToUserId(toUserId);

        when(dataProcessor.isValidToken(token, email)).thenReturn(false);

        boolean result = controller.getDirectsByUserId(token, userId, userName, email, dto);

        assertFalse(result);
    }

    @Test
    public void testGetMessages_validToken() {
        String token = "valid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        String id = "1";


        List<MessagesDTO> messages = List.of(new MessagesDTO());
        when(dataProcessor.isValidToken(token, email)).thenReturn(true);
        when(directService.getMessages(id, userName, userId)).thenReturn(messages);

        List<MessagesDTO> result = controller.getMessages(token, userId, userName, email, id, 0);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(directService, times(1)).getMessages(id, userName, userId);
    }

    @Test
    public void testGetMessages_invalidToken() {
        String token = "invalid_token";
        String userId = "user1";
        String userName = "User1";
        String email = "user1@example.com";
        String directId = "1";
        int page = 0;

        when(dataProcessor.isValidToken(token, email)).thenReturn(false);

        List<MessagesDTO> result = controller.getMessages(token, userId, userName, email, directId, page);

        assertTrue(result.isEmpty());
    }
}

