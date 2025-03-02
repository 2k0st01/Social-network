package com.example.eureka_likes_comments.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private Long postId;
    private String url;
}
