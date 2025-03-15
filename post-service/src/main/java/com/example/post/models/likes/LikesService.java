package com.example.post.models.likes;

import com.example.post.kafka.KafkaProducerService;
import com.example.post.models.post.Post;
import com.example.post.models.post.PostRepository;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LikesService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "hasLike", key = "#postID + ':' + #userId"),
            @CacheEvict(value = "likeCount", key = "#postID")
    })
    public boolean addOrRemoveLike(Long postID, Long userId) {
        Optional<Post> post = postRepository.findPostById(postID);
        if (post.isPresent()) {
            Optional<Like> like = likeRepository.findByOwnerIdAndPost(userId, post.get().getId());
            if (like.isPresent()) {
                likeRepository.delete(like.get());
                post.get().getLikes().remove(like.get());
                postRepository.save(post.get());
                kafkaProducerService.incDecRating(String.valueOf(userId), String.valueOf(post.get().getOwnId()), "UNLIKE");
                return false;
            }
            Like like1 = new Like();
            like1.setPost(post.get());
            like1.setOwnerId(userId);
            post.get().getLikes().add(like1);

            likeRepository.save(like1);
            postRepository.save(post.get());
            kafkaProducerService.incDecRating(String.valueOf(userId), String.valueOf(post.get().getOwnId()), "LIKE");
            return true;
        }
        return false;
    }

    @Cacheable(value = "hasLike", key = "#postId + ':' + #userId")
    @Transactional(readOnly = true)
    public boolean hasLikeInPost(Long postId, Long userId) {
        Optional<Post> post = postRepository.findPostById(postId);
        return post.isPresent() && post.get().getLikes().stream().anyMatch(a -> a.getOwnerId().equals(userId));
    }

    @Cacheable(value = "likeCount", key = "#postId")
    @Transactional(readOnly = true)
    public Integer getLikeCount(Long postId) {
        Optional<Post> post = postRepository.findPostById(postId);

        post.ifPresent(value -> System.out.println(value.getLikes().size()));

        return post.map(value -> value.getLikes().size()).orElse(0);
    }

}
