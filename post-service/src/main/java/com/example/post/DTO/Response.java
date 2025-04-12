package com.example.post.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private Long postId;
    private String url;
}
