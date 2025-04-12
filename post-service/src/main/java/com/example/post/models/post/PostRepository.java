package com.example.post.models.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
extends JpaRepository<Post, Long> {
    Optional<Post> findPostById(Long var1);

    List<Post> findPostsByOwnId(Long var1);

    Optional<Long> countPostByOwnId(Long var1);

    @Query(value="SELECT p FROM Post p WHERE p.time <= :twoWeeksAgo AND p.isNew = true")
    List<Post> findOldPosts(@Param(value="twoWeeksAgo") LocalDateTime var1);

    @Query(value="SELECT p FROM Post p WHERE p.ownId IN :ids AND p.isNew = true")
    List<Post> findNewPostsByOwnIds(@Param(value="ids") Set<Long> var1);
}
