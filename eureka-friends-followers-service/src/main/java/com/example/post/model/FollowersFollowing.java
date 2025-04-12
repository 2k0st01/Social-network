package com.example.post.model;

import com.example.post.model.follower.Follower;
import com.example.post.model.following.Following;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Entity
@Data
public class FollowersFollowing implements Serializable {
    @Id
    @Column(name="main_id")
    private Long id;

    @OneToMany(mappedBy = "followersFollowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follower> followers = new HashSet<>();

    @OneToMany(mappedBy = "followersFollowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Following> following = new HashSet<>();

    public void addToFollowers(Long id) {
        followers.add( new Follower(this, id) );
    }

    public void addToFollowing(Long id) {
        following.add( new Following(this, id) );
    }

    public void removeFromFollowers(Long followerId) {
        followers.removeIf(follower -> follower.getFollowerId().equals(followerId));
    }

    public void removeFromFollowing(Long followingId) {
        following.removeIf(f -> f.getFollowingId().equals(followingId));
    }

}
