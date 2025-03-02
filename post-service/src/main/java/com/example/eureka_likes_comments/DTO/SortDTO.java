package com.example.eureka_likes_comments.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SortDTO implements Serializable {
    private Long userID;
    private Integer rating;
}
