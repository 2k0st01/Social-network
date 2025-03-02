package com.example.eureka_friends_followers_service.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.Generated;

@Entity
@Data
public class FollowersFollowing implements Serializable {
    @Id
    @Column(name="main_id")
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="collection_followers", joinColumns={@JoinColumn(name="followers_following_id")})
    @Column(name="follower_id")
    private Set<Long> followers = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="collection_following", joinColumns={@JoinColumn(name="followers_following_id")})
    @Column(name="following_id")
    private Set<Long> following = new HashSet<>();

    public void addToFollowers(Long id) {
        followers.add(id);
    }

    public void addToFollowing(Long id) {
        following.add(id);
    }

    public void removeFromFollowers(Long id) {
        followers.remove(id);
    }

    public void removeFromFollowing(Long id) {
        following.remove(id);
    }


}
