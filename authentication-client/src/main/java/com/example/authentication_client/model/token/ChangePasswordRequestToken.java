package com.example.authentication_client.model.token;

import com.example.authentication_client.model.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Generated;

@Entity
@Data
public class ChangePasswordRequestToken {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String token;
    @Column(nullable=false)
    private LocalDateTime createdAt;
    @Column(nullable=false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(nullable=false, name="bank_account_id")
    private UserAccount userAccount;

    public ChangePasswordRequestToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, UserAccount userAccount) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAccount = userAccount;
    }

    @Generated
    public ChangePasswordRequestToken() {
    }
}
