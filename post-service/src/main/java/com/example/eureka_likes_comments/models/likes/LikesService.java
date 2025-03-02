package com.example.eureka_likes_comments.models.likes;

import com.example.eureka_likes_comments.kafka.KafkaProducerService;
import com.example.eureka_likes_comments.models.post.Post;
import com.example.eureka_likes_comments.models.post.PostService;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LikesService {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "hasLike", key = "#postID + ':' + #userId"),
            @CacheEvict(value = "likeCount", key = "#postID")
    })
    public boolean addOrRemoveLike(Long postID, Long userId) {
        Optional<Post> post = postService.findPostById(postID);
        if (post.isPresent()) {
            Optional<Like> like = likeRepository.findByOwnerIdAndPost(userId, post.get());
            if (like.isPresent()) {
                (post.get()).getLikes().remove(like.get());
                likeRepository.delete(like.get());
                kafkaProducerService.incDecRating(String.valueOf(userId), String.valueOf((post.get()).getOwnId()), "UNLIKE");
                return false;
            }
            like = Optional.of(new Like());
            like.get().setPost(post.get());
            like.get().setOwnerId(userId);
            (post.get()).getLikes().add(like.get());
            likeRepository.save(like.get());
            kafkaProducerService.incDecRating(String.valueOf(userId), String.valueOf((post.get()).getOwnId()), "LIKE");
            return true;
        }
        return false;
    }

}
