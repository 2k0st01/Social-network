package com.example.eurekaclient.direct;

import com.example.eurekaclient.messenger.Messages;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(
        indexes = {
                @Index(name = "idx_direct_users_combined", columnList = "firstUserId, secondUserId"),
                @Index(name = "idx_direct_firstUser", columnList = "firstUserId"),
                @Index(name = "idx_direct_secondUser", columnList = "secondUserId"),
                @Index(name = "idx_direct_lastUpdate", columnList = "lastTimeUpdate DESC")
        }
)
public class Direct {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String firstUserId;
    private String secondUserId;
    private String firstUserName;
    private String secondUserName;
    private String lastMassage;
    private LocalDateTime lastTimeUpdate;

    @OneToMany(mappedBy="direct", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    private List<Messages> messages = new ArrayList<>();

    public void addMessages(Messages messages) {
        this.messages.add(messages);
    }
}
