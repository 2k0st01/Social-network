package com.example.post.controller;

import com.example.post.DTO.CommentsDTO;
import com.example.post.DTO.PostDTO;
import com.example.post.DTO.Response;
import com.example.post.DTO.SortDTO;
import com.example.post.config.DataProcessor;
import com.example.post.models.avatar.AvatarService;
import com.example.post.models.comments.CommentService;
import com.example.post.models.likes.LikesService;
import com.example.post.models.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/post")
@AllArgsConstructor
public class Controller {
    private final DataProcessor dataProcessor;
    private final PostService postService;
    private final LikesService likesService;
    private final CommentService commentService;
    private final AvatarService avatarService;



    @PostMapping("/createAvatar")
    public ResponseEntity<Boolean> createAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-token") String token,
            @RequestHeader("X-User-email") String email,
            @RequestHeader("LINK") String postURL) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(avatarService.creatAvatarForUser(postURL, userId));
    }

    @GetMapping("/getUserAvatar")
    public ResponseEntity<String> getUserAvatar(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long userId) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body("");
        }
        return ResponseEntity.ok(avatarService.getAvatarUrlByOwnId(userId));
    }

    @PostMapping ("/getUserAvatars")
    public ResponseEntity<Map<Long,String>> getUserAvatars(
            @RequestBody Set<Long> id) {

        return ResponseEntity.ok(avatarService.getAvatarsUrlByOwnId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> createModelForNewPost(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-token") String token,
            @RequestHeader("X-User-email") String email,
            @RequestHeader("LINK") String postURL) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(postService.createNewPost(userId, postURL));
    }

    @GetMapping("/getPosts")
    public ResponseEntity<List<Response>> getPostsId(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long userId) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        return ResponseEntity.ok(postService.getPostIdsByLinks(userId));
    }

    @GetMapping("/countPosts/{id}")
    public ResponseEntity<Long> getPostsCounts(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @PathVariable("id") Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(0L);
        }
        return ResponseEntity.ok(postService.countPostByUserId(id));
    }

    @PostMapping("/addLike")
    public ResponseEntity<Boolean> addLike(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(likesService.addOrRemoveLike(id, userId));
    }

    @GetMapping("/hasLikeInPost")
    public ResponseEntity<Boolean> hasLikeInPost(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(likesService.hasLikeInPost(postId, userId));
    }

    @GetMapping("/likeCount")
    public ResponseEntity<Integer> likeCount(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(0);
        }
        return ResponseEntity.ok(likesService.getLikeCount(id));
    }

    @PostMapping("/addComment")
    public ResponseEntity<Boolean> addComment(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestBody String comment) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(commentService.addComment(id, userId, userName, comment));
    }

    @GetMapping("/getComment")
    public ResponseEntity<List<CommentsDTO>> getComments(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Email") String email,
            @RequestParam Long id) {
        if (!dataProcessor.isValidToken(token, email)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        return ResponseEntity.ok(commentService.getComments(id));
    }

    @PostMapping("/watched")
    public ResponseEntity<Void> watchCountIncrement(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long postID) {
        postService.watchCountIncrement(userId, postID);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getPostsForFeedNews")
    public ResponseEntity<List<PostDTO>> postDTOS(
            @RequestHeader("X-User-Id") Long userID,
            @RequestBody List<SortDTO> list) {
        return ResponseEntity.ok(postService.getPostsForFeedNews(list, userID));
    }

//    @PostMapping("/checkUsersWithPosts")
//    public ResponseEntity<List<Long>> checkUsersWithPosts(@RequestBody List<Long> userIDs) {
//        return ResponseEntity.ok(postService.findUsersWithRecentPosts(userIDs));
//    }
}
