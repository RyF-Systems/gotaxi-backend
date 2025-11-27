package com.ryfsystems.ryftaxi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryfsystems.ryftaxi.enums.MessageType;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ToString
public class ChatMessage {
    private MessageType type;
    private Object content;
    private String sender;
    private String roomId;
    private String timestamp;

    @Nullable
    private TaxiRideRequest taxiRideRequest;

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

    public ChatMessage(MessageType messageType, TaxiRideRequest startedRequest, String username) {
        this.type = messageType;
        this.content = startedRequest;
        this.sender = username;
    }
}
