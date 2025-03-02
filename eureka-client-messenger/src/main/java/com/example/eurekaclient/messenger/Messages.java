package com.example.eurekaclient.messenger;

import com.example.eurekaclient.direct.Direct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
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
