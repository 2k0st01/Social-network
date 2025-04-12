package com.example.rating_service.model.userRating;

import com.example.rating_service.DTO.SortDTO;
import com.example.rating_service.enums.RatingEnums;
import com.example.rating_service.model.rating.Ratings;
import com.example.rating_service.model.userRating.UserRatingRepository;
import com.example.rating_service.model.userRating.UsersRating;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserRatingService {
    private final UserRatingRepository userRatingRepository;

    @Transactional
    public UsersRating create(Long userID, Ratings rating, Long ownID) {
        UsersRating ratings = new UsersRating();
        ratings.setRatings(rating);
        ratings.setUserId(userID);
        ratings.setRating(0);
        ratings.setOwnId(ownID);
        userRatingRepository.save(ratings);
        return ratings;
    }

    public Optional<UsersRating> findUsersRatingByUserId(Long userID) {
        return userRatingRepository.findUsersRatingByUserId(userID);
    }

    @Transactional
    public UsersRating save(UsersRating usersRating) {
        return userRatingRepository.save(usersRating);
    }

    @Transactional
    public List<SortDTO> findByOwnIdSorted(Long ownID, int page) {
        PageRequest pageable = PageRequest.of(page, 10);
        return userRatingRepository.findByOwnIdSorted(ownID, pageable).map(a -> {
            SortDTO sortDTO = new SortDTO();
            sortDTO.setUserID(a.getUserId());
            sortDTO.setRating(a.getRating());
            return sortDTO;
        }).stream().collect(Collectors.toList());
    }

    @Transactional
    public void incrementDecrementRating(Long ownID, Long targetId, RatingEnums enums) {
        Map<RatingEnums, Integer> ratingChanges = Map.of(RatingEnums.LIKE, 10, RatingEnums.UNLIKE, -10, RatingEnums.COMMENT, 25, RatingEnums.MESSAGE, 1, RatingEnums.USER_VIEW, 3);
        Optional<UsersRating> rc = userRatingRepository.getUsersRatingByUserIdAndOwnId(targetId, ownID);
        if (rc.isPresent()) {
            rc.get().setRating(rc.get().getRating() + ratingChanges.getOrDefault(enums, 0));
            userRatingRepository.save(rc.get());
        }
    }

}
