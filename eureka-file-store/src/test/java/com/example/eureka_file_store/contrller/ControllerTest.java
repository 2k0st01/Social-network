package com.example.eureka_file_store.contrller;

import com.example.eureka_file_store.config.DataProcessor;
import com.example.eureka_file_store.contoller.Controller;
import com.example.eureka_file_store.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private DataProcessor dataProcessor;

    @InjectMocks
    private Controller controller;

    private MockMultipartFile mockFile;
    private final String validToken = "Bearer valid-token";
    private final Long userId = 1L;
    private final String userName = "testUser";
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );
    }

    @Test
    void uploadFile_Success_ReturnsOkWithUrl() throws IOException {
        List<String> expectedUrls = Collections.singletonList("https://s3.bucket.com/file.txt");
        when(dataProcessor.isValidToken(validToken, email)).thenReturn(true);
        when(s3Service.uploadFiles(Collections.singletonList(mockFile), userId)).thenReturn(expectedUrls);

        ResponseEntity<List<String>> response = controller.uploadFile(
                validToken, userId, userName, email, Collections.singletonList(mockFile)
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrls, response.getBody());
        verify(s3Service).uploadFiles(Collections.singletonList(mockFile), userId);
    }

    @Test
    void uploadFile_Unauthorized_Returns401() throws IOException {
        when(dataProcessor.isValidToken(validToken, email)).thenReturn(false);

        ResponseEntity<List<String>> response = controller.uploadFile(
                validToken, userId, userName, email, Collections.singletonList(mockFile)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(Collections.singletonList("Not authorized person."), response.getBody());
        verify(s3Service, never()).uploadFiles(anyList(), anyLong());
    }

    @Test
    void uploadFile_IOException_Returns500() throws IOException {
        when(dataProcessor.isValidToken(validToken, email)).thenReturn(true);
        when(s3Service.uploadFiles(anyList(), eq(userId))).thenThrow(new IOException("S3 error"));

        ResponseEntity<List<String>> response = controller.uploadFile(
                validToken, userId, userName, email, Collections.singletonList(mockFile)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Error uploading file: S3 error", response.getBody().get(0));
    }

    @Test
    void uploadUserPic_Success_ReturnsOkWithUrl() throws IOException {
        String expectedUrl = "https://s3.bucket.com/avatar.jpg";
        when(dataProcessor.isValidToken(validToken, email)).thenReturn(true);
        when(s3Service.uploadAvatarFile(mockFile, userId)).thenReturn(expectedUrl);

        ResponseEntity<String> response = controller.uploadUserPic(
                validToken, userId, userName, email, mockFile
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(s3Service).uploadAvatarFile(mockFile, userId);
    }

    @Test
    void listPhotosWithHeaders_Success_ReturnsOkWithList() {
        List<String> expectedList = Collections.singletonList("https://s3.bucket.com/photo.jpg");
        when(dataProcessor.isValidToken(validToken, email)).thenReturn(true);
        when(s3Service.listAllPhotos(userId)).thenReturn(expectedList);

        ResponseEntity<List<String>> response = controller.listPhotos(
                validToken, userId, userName, email
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedList, response.getBody());
        verify(s3Service).listAllPhotos(userId);
    }

    @Test
    void listPhotosById_Success_ReturnsList() {
        List<String> expectedList = Collections.singletonList("https://s3.bucket.com/photo.jpg");
        when(s3Service.listAllPhotos(userId)).thenReturn(expectedList);

        List<String> result = controller.listPhotos(userId);

        assertEquals(expectedList, result);
        verify(s3Service).listAllPhotos(userId);
    }
}
