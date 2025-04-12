package com.example.post.models.avatar;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="avatar", indexes = {
        @Index(name = "index_user_avatar_ownId",columnList = "own_id")
})
@Data
public class UserAvatar {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="own_id")
    private Long ownId;
    @Column(name="url")
    private String url;

    public UserAvatar() {
    }

    public UserAvatar(Long ownId, String url) {
        this.ownId = ownId;
        this.url = url;
    }
}
