package com.example.post.model.follower;

import com.example.post.model.FollowersFollowing;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
@Table(
        name = "followers",
        indexes = {
                @Index(name = "idx_follower_id", columnList = "follower_id"),
                @Index(name = "idx_followers_following_id", columnList = "followers_following_id"),
                @Index(name = "idx_follower_followersFollowing", columnList = "follower_id, followers_following_id")
        }
)

public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "followers_following_id")
    private FollowersFollowing followersFollowing;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    public Follower(FollowersFollowing followersFollowing, Long followerId) {
        this.followersFollowing = followersFollowing;
        this.followerId = followerId;
    }

    public Follower() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follower follower = (Follower) o;
        return Objects.equals(followerId, follower.followerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId);
    }

}
