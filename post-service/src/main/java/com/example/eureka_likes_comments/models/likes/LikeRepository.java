package com.example.eureka_likes_comments.models.likes;

import com.example.eureka_likes_comments.models.post.Post;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository
extends JpaRepository<Like, Long> {


    @Query("SELECT l FROM Like l WHERE l.ownerId = :ownerId AND l.post.id = :postId")
    Optional<Like> findByOwnerIdAndPost(@Param("ownerId") Long ownerId, @Param("postId") Long postId);

}
