package com.example.search_history.search;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="search", indexes = @Index(name = "search_index_own_id", columnList = "own_id"))
@Data
public class Search {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    private String username;
    private String userId;
    @Column(name = "own_id")
    private String ownId;
    private LocalDateTime time;
}
