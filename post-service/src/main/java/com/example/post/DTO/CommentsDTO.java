package com.example.post.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentsDTO implements Serializable {
    private Long id;
    private String username;
    private String comment;
    private LocalDateTime time;
}