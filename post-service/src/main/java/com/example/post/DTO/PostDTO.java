package com.example.post.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
@Data
public class PostDTO implements Serializable {
    private Long postID;
    private Long ownID;
    private String postURL;
    private Integer countLike;
    private Integer countWatch;
    private LocalDateTime time;
}