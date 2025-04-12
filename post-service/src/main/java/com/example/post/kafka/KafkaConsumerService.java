package com.example.post.kafka;

import com.example.post.models.avatar.AvatarService;
import com.example.post.models.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private final PostService postService;
    private final AvatarService avatarService;

    @KafkaListener(topics={"photo.uploaded"}, groupId="post-service")
    public void consumePhotoUploaded(String event) {
        String[] message = event.split(";");
        if (message.length == 2) {
            postService.createNewPost(Long.valueOf(message[1]), message[0]);
        } else {
            System.err.println("Received malformed message: " + event);
        }
    }

    @KafkaListener(topics={"avatar.uploaded"}, groupId="post-service")
    public void consumePhotoAvatarUploaded(String event) {
        String[] message = event.split(";");
        if (message.length == 2) {
            avatarService.creatAvatarForUser(message[0], Long.valueOf(message[1]));
        } else {
            System.err.println("Received malformed message: " + event);
        }
    }
}
