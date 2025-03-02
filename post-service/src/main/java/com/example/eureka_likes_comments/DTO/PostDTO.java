package com.example.eureka_likes_comments.DTO;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class PostDTO {
    private Long postID;
    private Long ownID;
    private String postURL;
    private Integer countLike;
    private Integer countWatch;
    private LocalDateTime time;
}