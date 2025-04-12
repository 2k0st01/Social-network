package com.example.rating_service.model.rating;

import com.example.rating_service.model.userRating.UsersRating;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;


@Entity
@Data
@Table(indexes = @Index(name = "idx_rating_own_id", columnList = "own_id"))
public class Ratings {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="own_id")
    private Long ratingOwnID;
    @OneToMany
    private Set<UsersRating> usersRating = new HashSet<>();
}
