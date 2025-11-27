package com.ryfsystems.ryftaxi.model;

import com.ryfsystems.ryftaxi.dto.ChatMessage;
import com.ryfsystems.ryftaxi.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType type;

    @Column(name = "content")
    private String content;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor para convertir desde ChatMessage
    public Message(ChatMessage chatMessage) {
        this.type = chatMessage.getType();
        this.content = chatMessage.getContent().toString();
        this.sender = chatMessage.getSender();
        this.roomId = chatMessage.getRoomId();
        this.timestamp = LocalDateTime.parse(chatMessage.getTimestamp());
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
