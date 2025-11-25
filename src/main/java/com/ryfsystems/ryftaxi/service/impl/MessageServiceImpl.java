package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.ChatMessage;
import com.ryfsystems.ryftaxi.model.Message;
import com.ryfsystems.ryftaxi.repository.MessageRepository;
import com.ryfsystems.ryftaxi.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public Message saveMessage(ChatMessage chatMessage) {
        try {
            Message message = new Message(chatMessage);
            return messageRepository.save(message);
        }  catch (Exception e) {
            log.error("❌ Error guardando mensaje en BD: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el mensaje", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getRoomMessageHistory(String roomId) {
        return messageRepository.findByRoomId(roomId)
                .stream()
                .map(this::convertToChatMessage)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentRoomMessages(String roomId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return messageRepository.findByRoomIdOrderByTimestampDesc(roomId, pageable)
                .getContent()
                .stream()
                .map(this::convertToChatMessage)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getRoomMessagesSince(String roomId, LocalDateTime since) {
        return messageRepository.findByRoomIdAndTimestampAfterOrderByTimestampAsc(roomId, since)
                .stream()
                .map(this::convertToChatMessage)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getMessageCountByRoom(String roomId) {
        return messageRepository.countByRoomId(roomId);
    }

    private ChatMessage convertToChatMessage(Message message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(message.getType());
        chatMessage.setContent(message.getContent());
        chatMessage.setSender(message.getSender());
        chatMessage.setRoomId(message.getRoomId());
        chatMessage.setTimestamp(message.getTimestamp().toString());
        return chatMessage;
    }
}
