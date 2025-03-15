package com.example.post.models.avatar;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository
extends JpaRepository<UserAvatar, Long> {
    Optional<UserAvatar> getUserAvatarByOwnId(Long var1);
    Optional<UserAvatar> findUserAvatarByOwnId(Long var1);
    List<UserAvatar> findByOwnIdIn(Set<Long> id);
}
