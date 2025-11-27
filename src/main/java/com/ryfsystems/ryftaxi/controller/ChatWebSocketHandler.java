package com.ryfsystems.ryftaxi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryfsystems.ryftaxi.dto.ChatMessage;
import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import com.ryfsystems.ryftaxi.enums.MessageType;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.model.UserRole;
import com.ryfsystems.ryftaxi.service.MessageService;
import com.ryfsystems.ryftaxi.service.TaxiRideService;
import com.ryfsystems.ryftaxi.service.UserRoleService;
import com.ryfsystems.ryftaxi.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Map<String, User>> roomUsers = new ConcurrentHashMap<>();

    private final Sinks.Many<ChatMessage> messageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<ChatMessage> messageFlux = messageSink.asFlux();

    private final MessageService messageService;
    private final TaxiRideService taxiRideService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    public ChatWebSocketHandler(MessageService messageService, TaxiRideService taxiRideService, UserService userService, UserRoleService userRoleService) {
        this.messageService = messageService;
        this.taxiRideService = taxiRideService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        // Crear y configurar ObjectMapper directamente aquí
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.findAndRegisterModules();

        log.debug("✅ ObjectMapper configurado con JavaTimeModule");
        log.debug("📋 Módulos registrados: " + objectMapper.getRegisteredModuleIds());
    }

    @PostConstruct
    public void init() {
        taxiRideService.loadActiveServicesToCache();
        log.info("🔄 Servicios activos cargados desde BD");
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("🔗 Conexión establecida: " + session.getId());

        // Suscribirse al flux de mensajes para este usuario con manejo de errores
        messageFlux
                .filter(message -> shouldSendToSession(session, message))
                .subscribe(
                        message -> sendMessageToSession(session, message),
                        error -> {
                            System.err.println("❌ Error en flux para sesión " + session.getId() + ": " + error.getMessage());
                            cleanupClosedSession(session.getId());
                        },
                        () -> log.debug("✅ Flux completado para sesión: " + session.getId())
                );
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            handleChatMessage(session, chatMessage);
        } catch (IOException e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            sendError(session, "Error procesando mensaje");
        }
    }

    private void handleChatMessage(WebSocketSession session, ChatMessage chatMessage) {
        switch (chatMessage.getType()) {
            case JOIN:
                handleJoin(session, chatMessage);
                break;
            case CHAT:
                handleChat(session, chatMessage);
                break;
            case LEAVE:
                handleLeave(session, chatMessage);
                break;
            case TYPING:
                handleTyping(session, chatMessage);
                break;
            case STOP_TYPING:
                handleStopTyping(session, chatMessage);
                break;
            case SERVICE_STATUS_UPDATE:
                handleServiceStarted(session, chatMessage);
                break;
        }
    }

    private void handleStopTyping(WebSocketSession session, ChatMessage chatMessage) {
        User user = users.get(session.getId());
        if (user != null) {
            chatMessage.setSender(user.getUsername());
            messageSink.tryEmitNext(chatMessage);
        }
    }

    private void handleJoin(WebSocketSession session, ChatMessage message) {
        User user = userService.findByUsername(message.getSender());
        user.setId(user.getId());
        user.setSessionId(session.getId());
        user.setCurrentRoom(message.getRoomId());
        users.put(session.getId(), user);

        roomUsers.computeIfAbsent(message.getRoomId(), k -> new ConcurrentHashMap<>())
                .put(session.getId(), user);

        ChatMessage joinMessage = new ChatMessage(
                MessageType.JOIN,
                user.getUsername() + " se ha unido a la sala",
                "System",
                message.getRoomId()
        );

        userService.updateRoomAndSession(user.getId(), joinMessage.getRoomId(), user.getSessionId());

        messageSink.tryEmitNext(joinMessage);
        log.debug("👤 Usuario " + user.getUsername() + " se unió a la sala: " + message.getRoomId());
    }

    private void handleChat(WebSocketSession session, ChatMessage message) {
        User user = users.get(session.getId());
        if (user != null) {
            message.setSender(user.getUsername());
            messageSink.tryEmitNext(message);
            messageService.saveMessage(message);
            log.debug("Mensaje enviado: " + message);
        }
    }

    private void handleLeave(WebSocketSession session, ChatMessage message) {
        User user = users.get(session.getId());
        if (user != null) {
            // Remover usuario de la sala
            Map<String, User> room = roomUsers.get(user.getCurrentRoom());
            if (room != null) {
                room.remove(session.getId());
            }

            // Notificar salida
            ChatMessage leaveMessage = new ChatMessage(
                    MessageType.LEAVE,
                    user.getUsername() + " ha dejado la sala",
                    "System",
                    user.getCurrentRoom()
            );

            messageSink.tryEmitNext(leaveMessage);
            users.remove(session.getId());
            userService.updateRoomAndSession(user.getId(), null, null);
            log.debug("Usuario " + user.getUsername() + " dejó la sala");
        }
    }

    private void handleTyping(WebSocketSession session, ChatMessage message) {
        User user = users.get(session.getId());
        if (user != null) {
            message.setSender(user.getUsername());
            messageSink.tryEmitNext(message);
        }
    }

    private void handleServiceRequest(WebSocketSession session, ChatMessage message) {
        try {
            User user = users.get(session.getId());
            if (user != null && userRoleService.existsByUserIdAndRoleId(user.getId(), 4L)) {
                sendError(session, "Solo los usuarios tipo rider pueden solicitar servicios");
                return;
            }

            TaxiRideRequest serviceRequest = message.getTaxiRideRequest();
            serviceRequest.setRiderId(user.getId());
            serviceRequest.setRiderName(user.getUsername());

            // Crear solicitud de servicio
            TaxiRideRequest createdRequest = taxiRideService.createServiceRequest(serviceRequest);

            // Crear mensaje de respuesta
            ChatMessage responseMessage = new ChatMessage(
                    MessageType.SERVICE_REQUEST,
                    createdRequest,
                    user.getUsername()
            );

            // Broadcast a todos los drivers conectados y disponibles
            broadcastToDrivers(responseMessage);

            // También enviar confirmación al rider
            ChatMessage confirmationMessage = new ChatMessage(
                    MessageType.SERVICE_STATUS_UPDATE,
                    createdRequest,
                    "System"
            );
            sendMessageToSession(session, confirmationMessage);

            log.info("🚖 Solicitud de servicio creada: {}", createdRequest.getRequestId());

        } catch (Exception e) {
            log.error("Error procesando solicitud de servicio: {}", e.getMessage());
            sendError(session, "Error al crear solicitud de servicio: " + e.getMessage());
        }
    }

    private void handleServiceStarted(WebSocketSession session, ChatMessage message) {
        User driver = users.get(session.getId());

        if (driver != null && userRoleService.existsByUserIdAndRoleId(driver.getId(), 3L)) {
            String requestId = message.getContent().toString();
            TaxiRideRequest startedRequest = taxiRideService.startService(requestId, driver.getId() );

            if (startedRequest != null) {
                // Notificar al rider
                ChatMessage notification = new ChatMessage(
                        MessageType.SERVICE_STATUS_UPDATE,
                        startedRequest,
                        driver.getUsername()
                );
                sendToUser(startedRequest.getRiderId(), notification);

                log.info("🚗 Servicio {} iniciado por driver {}", requestId, driver.getUsername());
            }
        }
    }

    private void sendToUser(Long userId, ChatMessage message) {
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user.getId().equals(userId)) {
                WebSocketSession userSession = sessions.get(user.getSessionId());
                if (userSession != null && userSession.isOpen()) {
                    sendMessageToSession(userSession, message);
                } else {
                    log.debug("⚠️ No se pudo enviar mensaje - Sesión cerrada para usuario: {}", userId);
                    cleanupClosedSession(user.getSessionId());
                }
                break;
            }
        }
    }

    private void broadcastToDrivers(ChatMessage message) {
        broadcastToUserType(3L, message);
        log.debug("📢 Mensaje broadcast a todos los drivers: {}", message.getType());
    }

    private void broadcastToUserType(Long userType, ChatMessage message) {
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), userType);
            if (userRole.getUserTypeId().equals(userType)) {
                WebSocketSession userSession = sessions.get(user.getSessionId());
                if (userSession != null && userSession.isOpen()) {
                    sendMessageToSession(userSession, message);
                } else {
                    log.debug("⚠️ Sesión cerrada para usuario: {}", user.getUsername());
                    cleanupClosedSession(user.getSessionId());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("🔌 Conexión cerrada: " + session.getId() + " - Razón: " + status.getCode() + " - " + status.getReason());

        User user = users.get(session.getId());
        if (user != null) {
            // Crear mensaje de salida
            ChatMessage leaveMessage = new ChatMessage(
                    MessageType.LEAVE,
                    user.getUsername() + " ha dejado la sala",
                    "System",
                    user.getCurrentRoom()
            );
            // Enviar mensaje de salida a otros usuarios (excluyendo la sesión cerrada)
            broadcastMessageToRoom(leaveMessage, user.getCurrentRoom(), session.getId());
        }
        // Limpiar la sesión
        cleanupClosedSession(session.getId());
    }

    private void broadcastMessageToRoom(ChatMessage message, String roomId, String excludeSessionId) {
        Map<String, User> room = roomUsers.get(roomId);
        if (room != null) {
            for (String sessionId : room.keySet()) {
                // Saltar la sesión excluida
                if (sessionId.equals(excludeSessionId)) {
                    continue;
                }

                WebSocketSession targetSession = sessions.get(sessionId);
                if (targetSession != null && targetSession.isOpen()) {
                    sendMessageToSession(targetSession, message);
                } else {
                    // Limpiar sesión inválida
                    cleanupClosedSession(sessionId);
                }
            }
        }
    }

    public void cleanupAllClosedSessions() {
        List<String> closedSessions = new ArrayList<>();

        for (String sessionId : sessions.keySet()) {
            WebSocketSession session = sessions.get(sessionId);
            if (session == null || !session.isOpen()) {
                closedSessions.add(sessionId);
            }
        }

        for (String sessionId : closedSessions) {
            cleanupClosedSession(sessionId);
        }

        if (!closedSessions.isEmpty()) {
            log.debug("🧹 Limpiadas " + closedSessions.size() + " sesiones cerradas");
        }
    }

    private boolean shouldSendToSession(WebSocketSession session, ChatMessage message) {
        if (session == null || !session.isOpen()) {
            return false;
        }

        User user = users.get(session.getId());
        return user != null &&
                user.getCurrentRoom() != null &&
                user.getCurrentRoom().equals(message.getRoomId());
    }

    private void sendMessageToSession(WebSocketSession socketSession, ChatMessage message) {
        try {
            if (socketSession != null && socketSession.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                log.debug("📤 Enviando mensaje JSON a " + socketSession.getId() + ": " + jsonMessage);

                synchronized (socketSession) {
                    socketSession.sendMessage(new TextMessage(jsonMessage));
                }
                log.debug("✅ Mensaje enviado exitosamente a sesión: " + socketSession.getId());
            } else {
                log.debug("⚠️ No se puede enviar mensaje - Sesión cerrada: " +
                        (socketSession != null ? socketSession.getId() : "null"));

                // Limpiar sesión cerrada
                if (socketSession != null) {
                    cleanupClosedSession(socketSession.getId());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error enviando mensaje a sesión " +
                    (socketSession != null ? socketSession.getId() : "null") + ": " + e.getMessage());

            // Limpiar sesión con error
            if (socketSession != null) {
                cleanupClosedSession(socketSession.getId());
            }
        }
    }

    private void cleanupClosedSession(String sessionId) {
        log.debug("🧹 Limpiando sesión cerrada: " + sessionId);

        // Remover usuario si existe
        User user = users.remove(sessionId);
        if (user != null) {
            log.debug("👤 Usuario removido: " + user.getUsername());

            // Remover de la sala
            if (user.getCurrentRoom() != null) {
                Map<String, User> room = roomUsers.get(user.getCurrentRoom());
                if (room != null) {
                    room.remove(sessionId);
                    log.debug("🚪 Usuario removido de la sala: " + user.getCurrentRoom());

                    // Si la sala queda vacía, limpiarla
                    if (room.isEmpty()) {
                        roomUsers.remove(user.getCurrentRoom());
                        log.debug("🗑️ Sala vacía removida: " + user.getCurrentRoom());
                    }
                }
            }
        }

        // Remover sesión
        sessions.remove(sessionId);
        log.debug("✅ Sesión limpiada: " + sessionId);
    }

    private void sendError(WebSocketSession session, String error) {
        try {
            ChatMessage errorMessage = new ChatMessage(
                    MessageType.CHAT,
                    error,
                    "System",
                    "global"
            );
            String jsonError = objectMapper.writeValueAsString(errorMessage);
            session.sendMessage(new TextMessage(jsonError));
        } catch (IOException ex) {
            log.debug("Error enviando mensaje de error: " + ex.getMessage());
        }
    }

    public List<ChatMessage> getRoomMessageHistory(String roomId) {
        return messageService.getRoomMessageHistory(roomId);
    }

    public List<ChatMessage> getRecentRoomMessages(String roomId, int limit) {
        return messageService.getRecentRoomMessages(roomId, limit);
    }

    public int getActiveUsersCount() {
        return users.size();
    }

    public int getActiveRoomsCount() {
        return roomUsers.size();
    }

    public int getTotalSessions() {
        return sessions.size();
    }

    public Map<String, Object> getDetailedStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeUsers", getActiveUsersCount());
        stats.put("activeRooms", getActiveRoomsCount());
        stats.put("totalSessions", getTotalSessions());
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Integer> roomDetails = new HashMap<>();
        for (Map.Entry<String, Map<String, User>> entry : roomUsers.entrySet()) {
            roomDetails.put(entry.getKey(), entry.getValue().size());
        }
        stats.put("rooms", roomDetails);

        List<String> userList = users.values().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        stats.put("connectedUsers", userList);

        return stats;
    }

    public Map<String, Object> getRoomInfo(String roomId) {
        Map<String, User> room = roomUsers.get(roomId);
        if (room == null) {
            return Map.of("error", "Sala no encontrada");
        }
        List<Map<Object, Object>> usersInRoom = room.values()
                .stream()
                .map(user -> {
                    Map<Object, Object> userInfo = new HashMap<>();
                    userInfo.put("username", user.getUsername());
                    userInfo.put("sessionId", user.getSessionId());
                    userInfo.put("userId", user.getId());
                    return userInfo;
                })
                .toList();
        return Map.of(
                "roomId", roomId,
                "userCount", room.size(),
                "users", usersInRoom,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "messageCount", getMessageStats().get(roomId)
        );
    }

    public Map<String, Object> getMessageStats() {
        Map<String, Object> stats = new HashMap<>();

        for (String roomId : roomUsers.keySet()) {
            long messageCount = messageService.getMessageCountByRoom(roomId);
            stats.put(roomId, messageCount);
        }
        return stats;
    }

    public Map<String, Object> getFullStats() {
        return taxiRideService.countServiceByStatus();
    }
}