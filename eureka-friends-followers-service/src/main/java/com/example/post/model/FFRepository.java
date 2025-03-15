package com.example.post.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FFRepository
extends JpaRepository<FollowersFollowing, Long> {


    @Query("SELECT f.followerId FROM Follower f WHERE f.followersFollowing.id = :id")
    Page<Long> findFollowersByIds(@Param("id") Long id, Pageable pageable);

    @Query("SELECT f.followingId FROM Following f WHERE f.followersFollowing.id = :id")
    Page<Long> findFollowingByIds(@Param("id") Long id, Pageable pageable);

    @Query("SELECT f.followerId FROM Follower f WHERE f.followersFollowing.id = :id")
    Set<Long> findFollowersById(@Param("id") Long id);

    @Query("SELECT f.followingId FROM Following f WHERE f.followersFollowing.id = :id")
    Set<Long> findFollowingById(@Param("id") Long id);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follower f " +
            "WHERE f.followerId = :ownID AND f.followersFollowing.id = :targetID")
    boolean existsFollower(@Param("ownID") Long ownID, @Param("targetID") Long targetID);

        @Query("""
        SELECT f.followersFollowing.id 
        FROM Follower f 
        GROUP BY f.followersFollowing.id 
        ORDER BY COUNT(f.followerId) DESC 
        LIMIT 10
    """)
        Set<Long> findTop10ByFollowers();

}
