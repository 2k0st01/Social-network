package com.example.eurekaclient.direct;

import com.example.eurekaclient.dto.DirectDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectRepository extends JpaRepository<Direct, Long> {

    boolean existsById(Long id);


    @Query("SELECT d FROM Direct d WHERE (d.firstUserId = :user1 AND d.secondUserId = :user2) OR (d.firstUserId = :user2 AND d.secondUserId = :user1)")
    Optional<Direct> findDirectByUserIds(@Param("user1") String user1, @Param("user2") String user2);

    @Query("SELECT new com.example.eurekaclient.dto.DirectDTO(d.id, d.lastMassage, d.lastTimeUpdate, " +
            "(CASE WHEN d.firstUserId = :userId THEN d.secondUserId ELSE d.firstUserId END), " +
            "(CASE WHEN d.firstUserName = :username THEN d.secondUserName ELSE d.firstUserName END)) " +
            "FROM Direct d WHERE d.firstUserId = :userId OR d.secondUserId = :userId")
    Page<DirectDTO> getAllDirectsByUserNameAsDTO(@Param("userId") String userId, @Param("username") String username, Pageable pageable);
}
