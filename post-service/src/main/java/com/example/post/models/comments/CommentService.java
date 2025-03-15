package com.example.post.models.comments;

import com.example.post.DTO.CommentsDTO;
import com.example.post.kafka.KafkaProducerService;
import com.example.post.models.post.Post;
import com.example.post.models.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    @CacheEvict(value = "comments", key = "#postID")
    public boolean addComment(Long postID, Long userId, String username, String comment) {
        Post post = postService.findPostById(postID)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postID));

        Comment newComment = new Comment();
        newComment.setUsername(username);
        newComment.setComment(comment);
        newComment.setLocalDateTime(LocalDateTime.now());
        newComment.setPost(post);
        newComment.setOwnId(userId);

        post.getComments().add(newComment);
        commentRepository.save(newComment);

        kafkaProducerService.incDecRating(String.valueOf(userId), String.valueOf(post.getOwnId()), "COMMENT");
        return true;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "comments", key = "#id")
    public List<CommentsDTO> getComments(Long id) {
        return postService.findPostById(id)
                .map(post -> post.getComments().stream().map(a -> {
                    CommentsDTO dto = new CommentsDTO();
                    dto.setComment(a.getComment());
                    dto.setUsername(a.getUsername());
                    dto.setId(a.getId());
                    dto.setTime(a.getLocalDateTime());
                    return dto;
                }).toList())
                .orElse(Collections.emptyList());
    }
}
