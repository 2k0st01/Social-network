package com.example.eureka_likes_comments.models.avatar;

import com.example.eureka_likes_comments.models.avatar.UserAvatar;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository
extends JpaRepository<UserAvatar, Long> {
    Optional<UserAvatar> getUserAvatarByOwnId(Long var1);
    Optional<UserAvatar> findUserAvatarByOwnId(Long var1);
}
