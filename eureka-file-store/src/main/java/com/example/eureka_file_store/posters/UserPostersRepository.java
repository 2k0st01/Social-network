package com.example.eureka_file_store.posters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostersRepository
extends JpaRepository<UserPorters, Long> {
}
