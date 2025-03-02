package com.example.eureka_friends_followers_service.model;

import com.example.eureka_friends_followers_service.config.DataProcessor;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class FFService {
    private final FFRepository repository;
    private final DataProcessor dataProcessor;


    @Transactional
    @CacheEvict(value = "userExist", allEntries = true)
    public boolean create(Long id) {
        FollowersFollowing followersFollowing = new FollowersFollowing();
        followersFollowing.setId(id);
        repository.save(followersFollowing);
        return true;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "allFollowings", key = "#ownID"),
            @CacheEvict(value = "allFollowers", key = "#targetID"),
            @CacheEvict(value = "followingCount", key = "#ownID"),
            @CacheEvict(value = "followersCount", key = "#targetID"),
            @CacheEvict(value = "hasFollow", key = "#ownID + ':' + #targetID"),
            @CacheEvict(value = "hasFollow", key = "#targetID + ':' + #ownID")
             })
    public String toggleFollow(Long targetID, Long ownID) {
        FollowersFollowing following = repository.findById(targetID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetID));
        FollowersFollowing follower = repository.findById(ownID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownID));

        boolean alreadyFollowing = following.getFollowers().contains(ownID);

        if (alreadyFollowing) {
            follower.removeFromFollowing(targetID);
            following.removeFromFollowers(ownID);
            dataProcessor.isAdd(ownID, targetID, false);
        } else {
            follower.addToFollowing(targetID);
            following.addToFollowers(ownID);
            dataProcessor.isAdd(ownID, targetID, true);
        }

        repository.save(following);
        repository.save(follower);

        return alreadyFollowing ? "Unfollowed" : "Followed";
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hasFollow", key = "#id + ':' + #userId")
    public boolean hasFollow(Long id, Long userId) {
        return repository.findById(id)
                .map(followersFollowing -> followersFollowing.getFollowers().contains(userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allFollowings", key = "#targetID")
    public Set<Long> getAllFollowing(Long targetID) {
        return repository.findFollowingById(targetID);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allFollowers", key = "#targetID")
    public Set<Long> getAllFollowers(Long targetID) {
        return repository.findFollowersById(targetID);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "followingCount", key = "#userId")
    public Integer getFollowingCount(Long userId) {
        return repository.findFollowingById(userId).size();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "followersCount", key = "#userId")
    public Integer getFollowersCount(Long userId) {
        return repository.findFollowersById(userId).size();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userExist", key = "#id")
    public boolean existsFollowersFollowingById(Long id) {
        return repository.existsFollowersFollowingById(id);
    }

}
