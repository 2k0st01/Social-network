package com.example.eureka_likes_comments.DTO;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Generated;
@Data
public class CommentsDTO {
    private Long id;
    private String username;
    private String comment;
    private LocalDateTime time;
}