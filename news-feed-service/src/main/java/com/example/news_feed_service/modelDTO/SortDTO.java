package com.example.news_feed_service.modelDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SortDTO implements Serializable {
    private Long userID;
    private Integer rating;
}
