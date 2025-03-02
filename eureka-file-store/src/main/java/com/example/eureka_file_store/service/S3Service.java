package com.example.eureka_file_store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.eureka_file_store.config.DataProcessor;
import com.example.eureka_file_store.kafka.KafkaProducerService;
import com.example.eureka_file_store.posters.UserPostersService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final UserPostersService userPostersService;
    private final KafkaProducerService kafkaProducerService;

    @Value("${S3ServiceStorage}")
    private String BUCKET_NAME;


    public List<String> uploadFiles(List<MultipartFile> files, long userId) throws IOException {
        String url = "photo";
        String userFolder = "user-data-storage/images/" + userId + "/";

        if (!amazonS3.doesObjectExist(BUCKET_NAME, userFolder)) {
            createFolder(userFolder);
        }

        return files.stream().map(file -> {
            try {
                String fileName = userFolder + System.currentTimeMillis() + "-" + userId + "-web-test";
                amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file.getInputStream(), new ObjectMetadata()));

                String fullURL = amazonS3.getUrl(BUCKET_NAME, fileName).toString();
                userPostersService.saveUserPoster(userId, fullURL);
                kafkaProducerService.sendPhotoUploadedEvent(url, fullURL, userId);

                return fullURL;
            } catch (IOException e) {
                throw new RuntimeException("Помилка під час завантаження файлу: " + file.getOriginalFilename(), e);
            }
        }).collect(Collectors.toList());
    }

    public String uploadAvatarFile(MultipartFile file, Long userId) throws IOException {
        String url = "avatar";
        String userFolder = "user-data-storage/userPic/" + userId + "/";
        String fileName = userFolder + System.currentTimeMillis() + "-" + userId + "-avatar";

        if (!amazonS3.doesObjectExist(BUCKET_NAME, userFolder)) {
            createFolder(userFolder);
        }

        List<S3ObjectSummary> existingFiles = amazonS3.listObjects(BUCKET_NAME, userFolder).getObjectSummaries();

        if(!existingFiles.isEmpty()) {
            for (S3ObjectSummary fileSummary : existingFiles) {
                amazonS3.deleteObject(BUCKET_NAME, fileSummary.getKey());
            }
        }

        amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file.getInputStream(), new ObjectMetadata()));

        String fullURL = amazonS3.getUrl(BUCKET_NAME, fileName).toString();
        kafkaProducerService.sendPhotoAvatarUploadedEvent(url, fullURL, userId);
        userPostersService.saveUserPoster(userId, fullURL);

        return fullURL;
    }

    public void createFolder(String folderName) {
        String folderKey = folderName.endsWith("/") ? folderName : folderName + "/";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        amazonS3.putObject(BUCKET_NAME, folderKey, new ByteArrayInputStream(new byte[0]), metadata);
    }

    public List<String> listAllPhotos(Long userId) {
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(BUCKET_NAME)
                .withPrefix("user-data-storage/images/" + userId + "/");

        List<String> photos = new ArrayList<>();

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(req);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String key = objectSummary.getKey();
                if (!key.endsWith("/")) {
                    try {
                        amazonS3.getObjectMetadata(BUCKET_NAME, key);
                        photos.add(amazonS3.getUrl(BUCKET_NAME, key).toString());
                    } catch (AmazonS3Exception e) {
                        System.err.println("File not found: " + key);
                    }
                }
            }
            req.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return photos;
    }
}
