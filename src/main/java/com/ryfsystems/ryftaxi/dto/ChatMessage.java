package com.ryfsystems.ryftaxi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryfsystems.ryftaxi.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ToString
@Schema(description = "Modelo para mensajes de chat y servicios WebSocket")
@NoArgsConstructor
public class ChatMessage {

    @Schema(description = "Tipo de mensaje", example = "CHAT", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageType type;

    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?")
    private Object content;

    @Schema(description = "Remitente del mensaje", example = "usuario123")
    private String sender;

    @Schema(description = "ID de la sala/room", example = "sala-general")
    private String roomId;

    @Schema(description = "Timestamp del mensaje en formato ISO", example = "2023-12-07T10:30:00")
    private String timestamp;

    @Nullable
    @Schema(description = "Información de solicitud de servicio de taxi")
    private TaxiRideRequest taxiRideRequest;

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
