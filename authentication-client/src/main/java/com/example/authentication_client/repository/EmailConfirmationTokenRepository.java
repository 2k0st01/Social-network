package com.example.authentication_client.repository;

import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.model.token.EmailConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {
    EmailConfirmationToken findConfirmationTokenByUserAccount(UserAccount var1);

    Optional<EmailConfirmationToken> findByToken(String var1);

    List<EmailConfirmationToken> findByConfirmedAtIsNull();

    void deleteByToken(String var1);

    @Query("UPDATE EmailConfirmationToken c SET c.confirmedAt = :confirmedAt WHERE c.token = :token")
    void updateConfirmedAt(@Param("token") String token, @Param("confirmedAt") LocalDateTime confirmedAt);

}
