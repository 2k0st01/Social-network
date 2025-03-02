package com.example.eureka_friends_followers_service.kafka;

import com.example.eureka_friends_followers_service.model.FFService;
import lombok.AllArgsConstructor;
import lombok.Generated;
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
