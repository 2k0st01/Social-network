package com.example.eurekaclient.direct;

import com.example.eurekaclient.messenger.Messages;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
