package com.example.post.models.avatar;

import java.util.*;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;

    @Transactional
    @CacheEvict(value = "avatar", key = "#ownId")
    public boolean creatAvatarForUser(String url, Long ownId) {
        Optional<UserAvatar> avatar = avatarRepository.findUserAvatarByOwnId(ownId);
        if (avatar.isPresent()) {
            avatar.get().setUrl(url);
        } else {
            avatarRepository.save(new UserAvatar(ownId, url));
        }
        return true;
    }

    @Transactional(readOnly=true)
    @Cacheable(value = "avatar", key = "#id")
    public String getAvatarUrlByOwnId(Long id) {
        return avatarRepository.getUserAvatarByOwnId(id)
                .map(UserAvatar::getUrl)
                .orElse("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4g_2Qj3LsNR-iqUAFm6ut2EQVcaou4u2YXw&s");
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getAvatarsUrlByOwnId(Set<Long> ids) {
        List<UserAvatar> list = avatarRepository.findByOwnIdIn(ids);

        Map<Long, String> map = new HashMap<>();

        list.forEach(a -> map.put(a.getOwnId(),
                Optional.ofNullable(a.getUrl())
                        .orElse("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4g_2Qj3LsNR-iqUAFm6ut2EQVcaou4u2YXw&s")));

        ids.forEach(id -> map.putIfAbsent(id,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4g_2Qj3LsNR-iqUAFm6ut2EQVcaou4u2YXw&s"));

        return map;
    }


}
