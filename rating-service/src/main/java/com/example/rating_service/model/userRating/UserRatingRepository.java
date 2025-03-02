package com.example.rating_service.model.userRating;

import com.example.rating_service.model.userRating.UsersRating;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRatingRepository
extends JpaRepository<UsersRating, Long> {
    Optional<UsersRating> getUsersRatingByUserIdAndOwnId(Long var1, Long var2);

    @Query(value="SELECT ur FROM UsersRating ur WHERE ur.ownId = :ownId ORDER BY ur.rating DESC")
    Page<UsersRating> findByOwnIdSorted(@Param(value="ownId") Long var1, Pageable var2);

    Optional<UsersRating> findUsersRatingByUserId(Long var1);
}
