package com.example.post.kafka;

import com.example.post.model.FFService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private final FFService ffService;

    @KafkaListener(topics={"new.user.account"}, groupId="eureka-friends-followers-service")
    public void consumePhotoUploaded(String event) {
        Long id = Long.valueOf(event);
        if (!ffService.existsFollowersFollowingById(id)) {
            ffService.create(id);
        }
    }

}
