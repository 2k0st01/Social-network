package com.example.rating_service.model.rating;

import com.example.rating_service.model.userRating.UsersRating;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;


@Entity
@Data
public class Ratings {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="own_id")
    private Long ratingOwnID;
    @OneToMany
    private Set<UsersRating> usersRating = new HashSet<>();
}
