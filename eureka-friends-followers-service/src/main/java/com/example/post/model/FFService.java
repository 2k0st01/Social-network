package com.example.post.model;

import com.example.post.DTO.UserDTO;
import com.example.post.config.DataProcessor;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        boolean alreadyFollowing = repository.existsFollower(ownID, targetID);

        if (alreadyFollowing) {
            follower.removeFromFollowing(targetID);
            following.removeFromFollowers(ownID);
            dataProcessor.isAdd(ownID, targetID, false);
        } else {
            follower.addToFollowing(targetID);
            following.addToFollowers(ownID);
            dataProcessor.isAdd(ownID, targetID, true);
        }

        repository.saveAll(List.of(following, follower));


        return alreadyFollowing ? "Unfollowed" : "Followed";
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hasFollow", key = "#id + ':' + #userId")
    public boolean hasFollow(Long id, Long userId) {
        return repository.existsFollower(userId, id);
    }


    @Transactional(readOnly = true)
    public List<UserDTO> getAllFollowing(Long targetID, Integer page) {
        if (targetID == null) {
            return Collections.emptyList();
        }

        PageRequest pageable = PageRequest.of(page, 50);

        Page<Long> followingIds = repository.findFollowingByIds(targetID, pageable);

        Map<Long, String> avatars = dataProcessor.getUsersAvatars(followingIds.toSet());
        Map<Long, String> names = dataProcessor.getUsersNames(followingIds.toSet());


        return followingIds.stream()
                .map(id -> new UserDTO(id, avatars.get(id), names.get(id)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllFollowers(Long targetID, Integer page) {
        if (targetID == null) {
            return Collections.emptyList();
        }

        PageRequest pageable = PageRequest.of(page, 50);

        Page<Long> followerIds = repository.findFollowersByIds(targetID, pageable);

        Map<Long, String> avatars = dataProcessor.getUsersAvatars(followerIds.toSet());
        Map<Long, String> names = dataProcessor.getUsersNames(followerIds.toSet());


        return followerIds.stream()
                .map(id -> new UserDTO(id, avatars.get(id), names.get(id)))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserDTO> findTop10ByFollowers() {
        Set<Long> set = repository.findTop10ByFollowers();

        Map<Long, String> avatars = dataProcessor.getUsersAvatars(set);
        Map<Long, String> names = dataProcessor.getUsersNames(set);


        return set.stream()
                .map(id -> new UserDTO(id, avatars.get(id), names.get(id)))
                .collect(Collectors.toList());
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
        return repository.existsById(id);  // JpaRepository вже має цей метод
    }




}
