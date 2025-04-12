package com.example.rating_service.controller;

import com.example.rating_service.DTO.SortDTO;
import com.example.rating_service.enums.RatingEnums;
import com.example.rating_service.model.rating.RatingService;
import com.example.rating_service.model.userRating.UserRatingService;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rating")
@AllArgsConstructor
public class Controller {
    private final RatingService ratingService;
    private final UserRatingService userRatingService;

    @PostMapping("/create")
    public boolean create(@RequestHeader(value="X-User-OwnID") Long ownID) {
        return ratingService.create(ownID).isPresent();
    }

    @PostMapping("/isAddToRating")
    public void add(@RequestHeader(value="X-User-OwnID") Long ownID,
                    @RequestHeader(value="X-Target-Id") Long targetId,
                    @RequestHeader(value="X-Is-Add") boolean isAdd) {
        ratingService.modifyUserInRatings(ownID, targetId, isAdd);
    }

    @PostMapping("/incDecRating")
    public void incDecRating(@RequestHeader(value="X-User-OwnID") Long ownID,
                             @RequestHeader(value="X-Target-Id") Long targetId,
                             @RequestParam RatingEnums ratingType) {
        userRatingService.incrementDecrementRating(ownID, targetId, ratingType);
    }

    @GetMapping("/findByOwnIdSorted")
    public List<SortDTO> findByOwnIdSorted(@RequestHeader(value="X-User-Id") Long ownID,
                                           @RequestParam(defaultValue="0") int page) {

        return userRatingService.findByOwnIdSorted(ownID, page);
    }

}
