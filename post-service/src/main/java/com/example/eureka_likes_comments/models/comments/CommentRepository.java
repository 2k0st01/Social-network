package com.example.eureka_likes_comments.models.comments;

import com.example.eureka_likes_comments.models.comments.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository
extends JpaRepository<Comment, Long> {
}
