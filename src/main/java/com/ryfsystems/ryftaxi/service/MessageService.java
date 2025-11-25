package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.ChatMessage;
import com.ryfsystems.ryftaxi.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    Message saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getRoomMessageHistory(String roomId);
    List<ChatMessage> getRecentRoomMessages(String roomId, int limit);
    List<ChatMessage> getRoomMessagesSince(String roomId, LocalDateTime since);
    long getMessageCountByRoom(String roomId);
}
