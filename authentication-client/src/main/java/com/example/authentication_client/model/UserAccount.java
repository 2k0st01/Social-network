package com.example.authentication_client.model;

import com.example.authentication_client.role.UserRoles;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import lombok.Data;
import lombok.Generated;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
public class UserAccount implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String username;
    private Boolean emailVerification = false;
    @Enumerated(value = EnumType.STRING)
    private UserRoles userRoles;

    public UserAccount() {

    }

    public UserAccount(@NonNull String email, @NonNull String password, @NonNull String username, UserRoles userRoles) {
        if (email == null) {
            throw new NullPointerException("email is marked non-null but is null");
        }
        if (password == null) {
            throw new NullPointerException("password is marked non-null but is null");
        }
        if (username == null) {
            throw new NullPointerException("username is marked non-null but is null");
        }
        this.email = email;
        this.password = password;
        this.username = username;
        this.userRoles = userRoles;
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.userRoles.name());
        return Collections.singletonList(authority);
    }

    public String getUsername() {
        return this.email;
    }

    public String getUsernameByUser() {
        return this.username;
    }


}
