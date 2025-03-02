package com.example.eureka_likes_comments.kafka;

import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void incDecRating(String id, String target, String type) {
        String message = id + ";" + target + ";" + type;
        kafkaTemplate.send("rating.event", message);
    }

}
