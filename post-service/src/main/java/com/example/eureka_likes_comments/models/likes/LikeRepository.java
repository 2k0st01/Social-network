package com.example.eureka_likes_comments.models.likes;

import com.example.eureka_likes_comments.models.likes.Like;
import com.example.eureka_likes_comments.models.post.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository
extends JpaRepository<Like, Long> {
    Optional<Like> findByOwnerIdAndPost(Long var1, Post var2);
}
