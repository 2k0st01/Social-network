package com.example.eureka_likes_comments.models.likes;

import com.example.eureka_likes_comments.models.post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Entity
@Table(name="likes")
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
