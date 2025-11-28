const users = require('../data/users');
const rides = require('../data/rides');

// In-memory state for socket sessions
const connectedUsers = new Map(); // socketId -> user
const roomUsers = new Map(); // roomId -> Set<socketId>

const setupSocket = (io) => {
    io.on('connection', (socket) => {
        console.log(`🔗 Conexión establecida: ${socket.id}`);

        socket.on('message', (message) => {
            // Handle different message types based on the 'type' field in the JSON payload
            // The Java client sends a JSON string as a message
            try {
                // If message is already an object, use it, otherwise parse it
                const chatMessage = typeof message === 'string' ? JSON.parse(message) : message;
                handleChatMessage(io, socket, chatMessage);
            } catch (e) {
                console.error("Error parsing message:", e);
            }
        });

        socket.on('disconnect', () => {
            console.log(`🔌 Conexión cerrada: ${socket.id}`);
            handleDisconnect(io, socket);
        });
    });
};

const handleChatMessage = (io, socket, message) => {
    switch (message.type) {
        case 'JOIN':
            handleJoin(io, socket, message);
            break;
        case 'CHAT':
            handleChat(io, socket, message);
            break;
        case 'LEAVE':
            handleLeave(io, socket, message);
            break;
        case 'TYPING':
            handleTyping(io, socket, message);
            break;
        case 'STOP_TYPING':
            handleStopTyping(io, socket, message);
            break;
        case 'SERVICE_REQUEST': // Custom type for service request logic if sent as message
            // Note: Java code handles SERVICE_REQUEST in a separate method but triggered? 
            // Actually Java code has `handleServiceRequest` but it's not in the switch case in `handleChatMessage` shown in `ChatWebSocketHandler.java` snippet?
            // Wait, looking at `ChatWebSocketHandler.java`:
            // `handleChatMessage` switch has: JOIN, CHAT, LEAVE, TYPING, STOP_TYPING, SERVICE_STATUS_UPDATE.
            // It DOES NOT have SERVICE_REQUEST in the switch.
            // BUT `handleServiceRequest` method exists.
            // Ah, maybe I missed where it's called. Or maybe it's handled via a specific message type that I missed or it's implicitly handled.
            // Let's re-read `ChatWebSocketHandler.java`.
            // Line 104: switch (chatMessage.getType()) ...
            // It handles SERVICE_STATUS_UPDATE -> handleServiceStarted.
            // Where is handleServiceRequest called?
            // It seems it might be missing from the switch in the provided snippet or I missed it.
            // However, `MessageType` enum has `SERVICE_REQUEST`.
            // I will assume `SERVICE_REQUEST` should be handled.
            handleServiceRequest(io, socket, message);
            break;
        case 'SERVICE_STATUS_UPDATE':
            handleServiceStarted(io, socket, message);
            break;
        default:
            console.log("Unknown message type:", message.type);
    }
};

const handleJoin = (io, socket, message) => {
    const user = users.find(u => u.username === message.sender);
    if (user) {
        // Update user session info
        connectedUsers.set(socket.id, { ...user, currentRoom: message.roomId });

        socket.join(message.roomId);

        if (!roomUsers.has(message.roomId)) {
            roomUsers.set(message.roomId, new Set());
        }
        roomUsers.get(message.roomId).add(socket.id);

        const joinMessage = {
            type: 'JOIN',
            content: `${user.username} se ha unido a la sala`,
            sender: 'System',
            roomId: message.roomId,
            timestamp: new Date().toISOString()
        };

        io.to(message.roomId).emit('message', joinMessage); // Broadcast to room
        console.log(`👤 Usuario ${user.username} se unió a la sala: ${message.roomId}`);
    }
};

const handleChat = (io, socket, message) => {
    const user = connectedUsers.get(socket.id);
    if (user) {
        message.sender = user.username;
        message.timestamp = new Date().toISOString();
        // Broadcast to room
        io.to(message.roomId).emit('message', message);
        console.log(`Mensaje enviado: ${message.content}`);
    }
};

const handleLeave = (io, socket, message) => {
    const user = connectedUsers.get(socket.id);
    if (user) {
        socket.leave(message.roomId);
        if (roomUsers.has(message.roomId)) {
            roomUsers.get(message.roomId).delete(socket.id);
        }

        const leaveMessage = {
            type: 'LEAVE',
            content: `${user.username} ha dejado la sala`,
            sender: 'System',
            roomId: message.roomId,
            timestamp: new Date().toISOString()
        };

        io.to(message.roomId).emit('message', leaveMessage);
        connectedUsers.delete(socket.id);
        console.log(`Usuario ${user.username} dejó la sala`);
    }
};

const handleTyping = (io, socket, message) => {
    const user = connectedUsers.get(socket.id);
    if (user) {
        message.sender = user.username;
        io.to(message.roomId).emit('message', message);
    }
};

const handleStopTyping = (io, socket, message) => {
    const user = connectedUsers.get(socket.id);
    if (user) {
        message.sender = user.username;
        io.to(message.roomId).emit('message', message);
    }
};

const handleServiceRequest = (io, socket, message) => {
    const user = connectedUsers.get(socket.id);
    // Check if user is rider (role 4)
    if (user && user.roles.includes(4)) {
        const serviceRequest = message.taxiRideRequest || message.content; // Adjust based on how client sends it
        serviceRequest.riderId = user.id;
        serviceRequest.riderName = user.username;
        serviceRequest.requestId = "REQ-" + Date.now();
        serviceRequest.status = "REQUESTED";

        // Save to rides (mock)
        rides.push(serviceRequest);

        const responseMessage = {
            type: 'SERVICE_REQUEST',
            content: serviceRequest, // Or taxiRideRequest field
            taxiRideRequest: serviceRequest,
            sender: user.username,
            timestamp: new Date().toISOString()
        };

        // Broadcast to all drivers
        // We need to find all sockets that belong to drivers (role 3)
        for (const [sId, u] of connectedUsers.entries()) {
            if (u.roles.includes(3)) {
                io.to(sId).emit('message', responseMessage);
            }
        }

        // Confirm to rider
        const confirmationMessage = {
            type: 'SERVICE_STATUS_UPDATE',
            content: serviceRequest,
            taxiRideRequest: serviceRequest,
            sender: 'System',
            timestamp: new Date().toISOString()
        };
        socket.emit('message', confirmationMessage);

        console.log(`🚖 Solicitud de servicio creada: ${serviceRequest.requestId}`);
    } else {
        sendError(socket, "Solo los usuarios tipo rider pueden solicitar servicios");
    }
};

const handleServiceStarted = (io, socket, message) => {
    const driver = connectedUsers.get(socket.id);
    if (driver && driver.roles.includes(3)) {
        const requestId = typeof message.content === 'string' ? message.content : message.content.requestId;

        const ride = rides.find(r => r.requestId === requestId);
        if (ride) {
            ride.status = "ACCEPTED"; // or STARTED
            ride.driverId = driver.id;
            ride.driverName = driver.username;
            ride.acceptedAt = new Date().toISOString();

            const notification = {
                type: 'SERVICE_STATUS_UPDATE',
                content: ride,
                taxiRideRequest: ride,
                sender: driver.username,
                timestamp: new Date().toISOString()
            };

            // Notify rider
            // Find rider socket
            for (const [sId, u] of connectedUsers.entries()) {
                if (u.id === ride.riderId) {
                    io.to(sId).emit('message', notification);
                    break;
                }
            }

            console.log(`🚗 Servicio ${requestId} iniciado por driver ${driver.username}`);
        }
    }
};

const handleDisconnect = (io, socket) => {
    const user = connectedUsers.get(socket.id);
    if (user) {
        const roomId = user.currentRoom;
        if (roomId) {
            const leaveMessage = {
                type: 'LEAVE',
                content: `${user.username} ha dejado la sala`,
                sender: 'System',
                roomId: roomId,
                timestamp: new Date().toISOString()
            };
            io.to(roomId).emit('message', leaveMessage);

            if (roomUsers.has(roomId)) {
                roomUsers.get(roomId).delete(socket.id);
            }
        }
        connectedUsers.delete(socket.id);
    }
};

const sendError = (socket, errorMsg) => {
    const errorMessage = {
        type: 'CHAT', // Using CHAT type for errors as in Java code
        content: errorMsg,
        sender: 'System',
        roomId: 'global',
        timestamp: new Date().toISOString()
    };
    socket.emit('message', errorMessage);
};

module.exports = setupSocket;
