package com.example.authentication_client.kafka;

import lombok.Generated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCreatNewUserAccountEvent(Long userId) {
        String message = String.valueOf(userId);
        kafkaTemplate.send("new.user.account", message);
    }

    @Generated
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
