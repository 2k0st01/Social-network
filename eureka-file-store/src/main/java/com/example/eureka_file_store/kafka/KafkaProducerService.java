package com.example.eureka_file_store.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPhotoUploadedEvent(String event, String postURL, Long userId) {
        String message = postURL + ";" + userId;
        kafkaTemplate.send(event + ".uploaded", message);
    }

    public void sendPhotoAvatarUploadedEvent(String event, String postURL, Long userId) {
        String message = postURL + ";" + userId;
        kafkaTemplate.send(event + ".uploaded", message);
    }


}
