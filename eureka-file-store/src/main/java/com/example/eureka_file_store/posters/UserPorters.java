package com.example.eureka_file_store.posters;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;

@Entity
@Data
public class UserPorters {
    @Id
    @Column(name="user_id")
    private Long id;
    @Column(name="poster_URL")
    @ElementCollection
    private List<String> posterURL = new ArrayList<String>();

    public UserPorters(Long id, String posterURL) {
        this.id = id;
        this.posterURL.add(posterURL);
    }

    public UserPorters() {
    }

    void addPoster(String posterURL) {
        this.posterURL.add(posterURL);
    }
}
