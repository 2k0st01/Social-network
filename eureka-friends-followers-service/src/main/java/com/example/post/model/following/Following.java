package com.example.post.model.following;

import com.example.post.model.FollowersFollowing;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
@Table(
        name = "following",
        indexes = {
                @Index(name = "idx_following_id", columnList = "following_id"),
                @Index(name = "idx_followers_following_id", columnList = "followers_following_id")
        }
)

public class Following {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "followers_following_id")
    private FollowersFollowing followersFollowing;

    @Column(name = "following_id", nullable = false)
    private Long followingId;

    public Following(FollowersFollowing followersFollowing, Long followingId) {
        this.followersFollowing = followersFollowing;
        this.followingId = followingId;
    }

    public Following() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Following follower = (Following) o;
        return Objects.equals(followingId, follower.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followingId);
    }

}
