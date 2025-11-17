package com.ryfsystems.ryftaxi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryfsystems.ryftaxi.enums.MessageType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String roomId;
    private String timestamp;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @JsonCreator
    public ChatMessage(
            @JsonProperty("type") MessageType type,
            @JsonProperty("content") String content,
            @JsonProperty("sender") String sender,
            @JsonProperty("roomId") String roomId) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.roomId = roomId;
        this.timestamp = this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
