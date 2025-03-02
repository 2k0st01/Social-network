package com.example.eureka_friends_followers_service.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FFRepository
extends JpaRepository<FollowersFollowing, Long> {

    @Query("SELECT f.followers FROM FollowersFollowing f WHERE f.id = :id")
    Set<Long> findFollowersById(@Param("id") Long id);

    @Query("SELECT f.following FROM FollowersFollowing f WHERE f.id = :id")
    Set<Long> findFollowingById(@Param("id") Long id);

    boolean existsFollowersFollowingById(Long var1);
}
