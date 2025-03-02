package com.example.eureka_file_store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.eureka_file_store.kafka.KafkaProducerService;
import com.example.eureka_file_store.posters.UserPostersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private UserPostersService userPostersService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private S3Service fileStoreService;

    @Value("${S3ServiceStorage}")
    private String BUCKET_NAME;

    @Test
    void uploadFiles_ShouldUploadToS3AndReturnUrls() throws IOException {
        long userId = 123L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        List<MultipartFile> files = List.of(mockFile);
        String bucketName = BUCKET_NAME;
        String filePath = "user-data-storage/images/" + userId + "/123456-web-test";
        URL mockUrl = new URL("https://s3.amazonaws.com/" + bucketName + "/" + filePath);

        when(amazonS3.getUrl(eq(bucketName), anyString())).thenReturn(mockUrl);

        List<String> result = fileStoreService.uploadFiles(files, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUrl.toString(), result.get(0));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(userPostersService, times(1)).saveUserPoster(eq(userId), eq(mockUrl.toString()));
        verify(kafkaProducerService, times(1)).sendPhotoUploadedEvent(anyString(), eq(mockUrl.toString()), eq(userId));
    }

    @Test
    void uploadFiles_EmptyList_ShouldReturnEmptyList() throws IOException {
        long userId = 123L;
        List<MultipartFile> files = Collections.emptyList();

        List<String> result = fileStoreService.uploadFiles(files, userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFiles_IOException_ShouldThrowRuntimeException() throws IOException {
        long userId = 123L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenThrow(new IOException("S3 error"));
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        List<MultipartFile> files = List.of(mockFile);
        String userFolder = "user-data-storage/images/" + userId + "/";
        when(amazonS3.doesObjectExist(BUCKET_NAME, userFolder)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                fileStoreService.uploadFiles(files, userId));
        assertEquals("Помилка під час завантаження файлу: test.jpg", exception.getMessage());
        verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadAvatarFile_ShouldUploadToS3AndReturnUrl() throws IOException {
        Long userId = 123L;
        String userFolder = "user-data-storage/userPic/" + userId + "/";
        String fileName = userFolder + System.currentTimeMillis() + "-" + userId + "-avatar";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));

        when(amazonS3.doesObjectExist(BUCKET_NAME, userFolder)).thenReturn(true);

        S3ObjectSummary mockFileSummary = new S3ObjectSummary();
        mockFileSummary.setKey(userFolder + "old-file-avatar");
        List<S3ObjectSummary> existingFiles = Collections.singletonList(mockFileSummary);

        ObjectListing mockListing = mock(ObjectListing.class);
        when(amazonS3.listObjects(BUCKET_NAME, userFolder)).thenReturn(mockListing);
        when(mockListing.getObjectSummaries()).thenReturn(existingFiles);


        URL mockUrl = new URL("https://s3.amazonaws.com/" + BUCKET_NAME + "/" + fileName);
        when(amazonS3.getUrl(any(), any())).thenReturn(mockUrl);

        String result = fileStoreService.uploadAvatarFile(mockFile, userId);

        assertNotNull(result);
        assertEquals(mockUrl.toString(), result);

        verify(amazonS3, times(1)).deleteObject(BUCKET_NAME, mockFileSummary.getKey());

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));

        verify(userPostersService, times(1)).saveUserPoster(eq(userId), eq(mockUrl.toString()));
        verify(kafkaProducerService, times(1)).sendPhotoAvatarUploadedEvent(eq("avatar"), eq(mockUrl.toString()), eq(userId));
    }


}
