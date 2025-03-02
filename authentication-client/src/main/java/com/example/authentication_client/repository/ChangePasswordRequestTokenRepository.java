package com.example.authentication_client.repository;

import com.example.authentication_client.model.token.ChangePasswordRequestToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangePasswordRequestTokenRepository extends JpaRepository<ChangePasswordRequestToken,Long> {
    ChangePasswordRequestToken findChangePasswordRequestTokenByToken(String token);
    void deleteChangePasswordRequestTokenByToken(String token);

    List<ChangePasswordRequestToken> findByConfirmedAtIsNull ();
}
