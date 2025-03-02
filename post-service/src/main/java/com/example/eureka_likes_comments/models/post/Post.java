package com.example.eureka_likes_comments.models.post;

import com.example.eureka_likes_comments.models.comments.Comment;
import com.example.eureka_likes_comments.models.likes.Like;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;


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
    @OneToMany
    @JoinColumn(name="likes")
    private Set<Like> likes = new HashSet<Like>();
    @ElementCollection
    @CollectionTable(name="post_watch_count", joinColumns={@JoinColumn(name="post_id")})
    @MapKeyColumn(name="user_id")
    @Column(name="watch_count")
    private Map<Long, Integer> watchCount = new HashMap<Long, Integer>();
    @OneToMany
    @JoinColumn(name="comments")
    private List<Comment> comments = new ArrayList<Comment>();
    private boolean isNew = true;

}
