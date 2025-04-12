package com.example.post.models.comments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository
extends JpaRepository<Comment, Long> {
}
