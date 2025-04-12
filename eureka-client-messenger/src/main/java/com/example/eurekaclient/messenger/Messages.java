package com.example.eurekaclient.messenger;

import com.example.eurekaclient.direct.Direct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        indexes = {
                @Index(name = "idx_messages_direct_time", columnList = "direct_id, time DESC"),
                @Index(name = "idx_messages_user_name", columnList = "userName")
        }
)
public class Messages {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String messenger;
    private LocalDateTime time;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="direct_id")
    @JsonIgnore
    private Direct direct;
}
