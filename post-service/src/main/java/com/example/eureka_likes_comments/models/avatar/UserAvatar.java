package com.example.eureka_likes_comments.models.avatar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="avatar")
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
