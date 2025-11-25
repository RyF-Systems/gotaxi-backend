package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomIdOrderByTimestampAsc(String roomId);
    Page<Message> findByRoomIdOrderByTimestampDesc(String roomId, Pageable pageable);
    List<Message> findByRoomIdAndTimestampAfterOrderByTimestampAsc(String roomId, LocalDateTime timestamp);
    List<Message> findByRoomId(String roomId);
    long countByRoomId(String roomId);
    List<Message> findBySender(String sender);
}
