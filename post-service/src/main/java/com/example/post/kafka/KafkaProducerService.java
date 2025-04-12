package com.example.post.kafka;

import lombok.AllArgsConstructor;
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
