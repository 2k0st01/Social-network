package com.example.post.modelDTO;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PostDTO implements Serializable {
    private Long postID;
    private Long ownID;
    private String postURL;
    private Integer countLike;
    private Integer countWatch;
    private LocalDateTime time;
}
