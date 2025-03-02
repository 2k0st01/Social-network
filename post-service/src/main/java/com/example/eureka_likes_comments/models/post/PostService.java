package com.example.eureka_likes_comments.models.post;

import com.example.eureka_likes_comments.DTO.PostDTO;
import com.example.eureka_likes_comments.DTO.Response;
import com.example.eureka_likes_comments.DTO.SortDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "count", key = "#userId"),
            @CacheEvict(value = "posts", key = "#userId")
    })
    public Boolean createNewPost(Long userId, String postURL) {
        Post post = new Post();
        post.setPostURL(postURL);
        post.setOwnId(userId);
        post.setTime(LocalDateTime.now());
        postRepository.save(post);
        return true;
    }


    @Transactional(readOnly = true)
    public Optional<Post> findPostById(Long id) {
        return postRepository.findPostById(id);
    }

    @Cacheable(value = "posts", key = "#userId")
    @Transactional(readOnly = true)
    public List<Response> getPostIdsByLinks(Long userId) {
        return postRepository.findPostsByOwnId(userId).stream().map(a -> {
            Response re = new Response();
            re.setUrl(a.getPostURL());
            re.setPostId(a.getId());
            return re;
        }).collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    @Cacheable(value = "count", key = "#userId")
    public Long countPostByUserId(Long userId) {
        return postRepository.countPostByOwnId(userId).orElse(0L);
    }

    @Transactional
    public void watchCountIncrement(Long userID, Long postID) {
        Optional<Post> post = postRepository.findPostById(postID);
        if (post.isPresent()) {
            Map<Long, Integer> watchCount = post.get().getWatchCount();
            watchCount.put(userID, watchCount.getOrDefault(userID, 0) + 1);
            postRepository.save(post.get());
        }
    }

    @Transactional
    public List<PostDTO> getPostsForFeedNews(List<SortDTO> list, Long userID) {
        Set<Long> userIDs = list.stream().map(SortDTO::getUserID).collect(Collectors.toSet());
        List<Post> posts = postRepository.findNewPostsByOwnIds(userIDs);
        List<Post> filteredPosts = posts.stream().filter(post -> post.getLikes().stream().noneMatch(like -> like.getOwnerId().equals(userID))).toList();
        return filteredPosts.stream().map(a -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostID(a.getId());
            postDTO.setOwnID(a.getOwnId());
            postDTO.setPostURL(a.getPostURL());
            postDTO.setTime(a.getTime());
            postDTO.setCountLike(a.getLikes().size());
            postDTO.setCountWatch(a.getWatchCount().get(userID));
            return postDTO;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hasLike", key = "#postId + ':' + #userId")
    public boolean hasLikeInPost(Long postId, Long userId) {
        Optional<Post> post = postRepository.findPostById(postId);
        return post.isPresent() && post.get().getLikes().stream().anyMatch(a -> a.getOwnerId().equals(userId));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "likeCount", key = "#postId")
    public Integer getLikeCount(Long postId) {
        Optional<Post> post = postRepository.findPostById(postId);
        return post.map(value -> value.getLikes().size()).orElse(0);
    }


//    @Transactional(readOnly = true)
//    public List<Long> findUsersWithRecentPosts(List<Long> userIDs) {
//        return postRepository.findUsersWithRecentPosts(userIDs);
//    }


    @Scheduled(fixedRate = 43200000L)
    @Transactional
    public void isNewPosts() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2L);
        List<Post> posts = getPostsOlderThanTwoWeeks(twoWeeksAgo);
        posts.forEach(a -> a.setNew(false));
        postRepository.saveAll(posts);
    }

    @Transactional
    public List<Post> getPostsOlderThanTwoWeeks(LocalDateTime twoWeeksAgo) {
        return postRepository.findOldPosts(twoWeeksAgo);
    }

}
