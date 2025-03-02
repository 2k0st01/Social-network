package com.example.eureka_likes_comments.models.post;

import com.example.eureka_likes_comments.models.comments.Comment;
import com.example.eureka_likes_comments.models.likes.Like;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;
    @Column(name="own_id")
    private Long ownId;
    @Column(name="postURL")
    private String postURL;
    private LocalDateTime time;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="likes")
    private Set<Like> likes = new HashSet<Like>();

    @ElementCollection
    @CollectionTable(name="post_watch_count", joinColumns={@JoinColumn(name="post_id")})
    @MapKeyColumn(name="user_id")
    @Column(name="watch_count")
    private Map<Long, Integer> watchCount = new HashMap<Long, Integer>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="comments")
    private List<Comment> comments = new ArrayList<Comment>();
    private boolean isNew = true;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(ownId, post.ownId) && Objects.equals(postURL, post.postURL) && Objects.equals(time, post.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownId, postURL, time);
    }
}
