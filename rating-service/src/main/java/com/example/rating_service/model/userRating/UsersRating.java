package com.example.rating_service.model.userRating;

import com.example.rating_service.model.rating.Ratings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Data;


@Entity
@Table(name="user_rating_table")
@Data
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
