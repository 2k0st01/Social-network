package com.example.post.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SortDTO implements Serializable {
    private Long userID;
    private Integer rating;
}
