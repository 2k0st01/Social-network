package com.example.rating_service.model.rating;

import com.example.rating_service.model.rating.Ratings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository
extends JpaRepository<Ratings, Long> {
    Optional<Ratings> findRatingsByRatingOwnID(Long var1);

    boolean existsRatingsByRatingOwnID(Long var1);
}
