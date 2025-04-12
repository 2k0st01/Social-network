package com.example.post.modelDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SortDTO implements Serializable {
    private Long userID;
    private Integer rating;
}
