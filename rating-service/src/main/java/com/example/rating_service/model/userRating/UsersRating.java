package com.example.rating_service.model.userRating;

import com.example.rating_service.model.rating.Ratings;
import jakarta.persistence.*;

import java.util.Objects;
import lombok.Data;


@Entity
@Data
@Table(name = "user_rating_table", indexes = {
        @Index(name = "idx_user_rating_user_id", columnList = "user_id"),
        @Index(name = "idx_user_rating_own_id", columnList = "own_id"),
        @Index(name = "idx_user_rating_user_id_own_id", columnList = "user_id, own_id") // Композитний індекс
})
public class UsersRating {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name="own_id")
    private Long ownId;
    @Column(name="user_rating")
    private Integer rating;
    @ManyToOne(fetch=FetchType.LAZY)
    private Ratings ratings;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UsersRating that = (UsersRating)o;
        return Objects.equals(this.id, that.id);
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

}
