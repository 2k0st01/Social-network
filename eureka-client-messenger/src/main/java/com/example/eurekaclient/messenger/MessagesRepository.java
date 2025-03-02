package com.example.eurekaclient.messenger;

import com.example.eurekaclient.dto.MessagesDTO;
import com.example.eurekaclient.messenger.Messages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessagesRepository
extends JpaRepository<Messages, Long> {

    @Query("""
            SELECT new com.example.eurekaclient.dto.MessagesDTO
            (m.userName,m.messenger, m.id, m.time,CASE WHEN m.userName = :username THEN 1 ELSE 2 END)
            FROM Messages m WHERE m.direct.id = :directId
        """)
    Slice<MessagesDTO> getMessageDTObyDirectId(@Param("directId") Long directId, @Param("username") String username, Pageable pageable);

}
