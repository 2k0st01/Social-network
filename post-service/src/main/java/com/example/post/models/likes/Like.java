package com.example.post.models.likes;

import com.example.post.models.post.Post;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "likes", indexes = {
        @Index(name = "idx_like_owner_post", columnList = "owner_id, post_id")
})
@Data
public class Like {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="like_id")
    private Long id;
    @Column(name="owner_id")
    private Long ownerId;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id) && Objects.equals(ownerId, like.ownerId) && Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, post);
    }
}
