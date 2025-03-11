package com.example.authentication_client.repository;

import com.example.authentication_client.model.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserAccountRepository
extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findUserAccountByEmail(String var1);
    boolean existsUserAccountById(Long var1);

    boolean existsUserAccountByEmailOrUsername(String var1, String var2);

    Optional<UserAccount> findUserAccountById(Long var1);


    @Query("UPDATE UserAccount a SET a.emailVerification = true WHERE a.email = :email")
    void enableAppUser(@Param("email") String email);

    List<UserAccount> findTop10UserAccountByUsernameStartingWith(String var1);

    List<UserAccount> findByIdIn(Set<Long> id);
}
