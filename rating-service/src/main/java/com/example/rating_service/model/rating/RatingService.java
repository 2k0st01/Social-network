package com.example.rating_service.model.rating;

import com.example.rating_service.model.userRating.UserRatingService;
import com.example.rating_service.model.userRating.UsersRating;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRatingService userRatingService;

    @Transactional
    public Optional<Ratings> create(Long ownID) {
        Ratings ratings = new Ratings();
        ratings.setRatingOwnID(ownID);
        return Optional.of(ratingRepository.save(ratings));
    }

    @Transactional(readOnly=true)
    public boolean existsRatingsByRatingOwnID(Long id) {
        return ratingRepository.existsRatingsByRatingOwnID(id);
    }

    @Transactional
    public void modifyUserInRatings(Long ownID, Long targetId, boolean isAdd) {
        Optional<Ratings> ratings = ratingRepository.findRatingsByRatingOwnID(ownID);
        if (ratings.isEmpty() && (ratings = create(ownID)).isEmpty()) {
            return;
        }
        Optional<UsersRating> usr = userRatingService.findUsersRatingByUserId(targetId);
        if (isAdd && usr.isEmpty()) {
            UsersRating ur = userRatingService.create(targetId, ratings.get(), ownID);
            ratings.get().getUsersRating().add(ur);
        } else if (usr.isPresent()) {
            ratings.get().getUsersRating().remove(usr.get());
        }
        ratingRepository.save(ratings.get());
    }
}
