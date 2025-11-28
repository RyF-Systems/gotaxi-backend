const io = require('socket.io-client');
const axios = require('axios');

const BASE_URL = 'http://localhost:8080';
const WS_URL = 'http://localhost:8080'; // Socket.io URL

async function runTest() {
    try {
        console.log("1. Testing Login...");
        const loginResponse = await axios.post(`${BASE_URL}/api/auth/login`, {
            username: "rider",
            password: "password123"
        });

        if (loginResponse.data.success) {
            console.log("✅ Login Successful:", loginResponse.data.username);
        } else {
            console.error("❌ Login Failed:", loginResponse.data);
            return;
        }

        console.log("\n2. Testing WebSocket Connection...");
        const socket = io(WS_URL, {
            path: '/ws/chat', // Matching server config
            transports: ['websocket']
        });

        socket.on('connect', () => {
            console.log("✅ Socket Connected:", socket.id);

            // Join Room
            const joinMessage = {
                type: 'JOIN',
                sender: 'rider',
                roomId: 'room-1'
            };
            socket.emit('message', joinMessage);
        });

        socket.on('message', (msg) => {
            console.log("📩 Message Received:", msg);

            if (msg.type === 'JOIN' && msg.sender === 'System') {
                console.log("✅ Joined Room Successfully");

                // Request Service
                console.log("\n3. Testing Service Request...");
                const serviceRequest = {
                    type: 'SERVICE_REQUEST',
                    content: {
                        pickupAddress: "123 Main St",
                        destinationAddress: "456 Market St",
                        pickupLat: 10.0,
                        pickupLng: 20.0,
                        destinationLat: 10.1,
                        destinationLng: 20.1,
                        estimatedPrice: 15.50
                    },
                    sender: 'rider',
                    roomId: 'room-1'
                };
                socket.emit('message', serviceRequest);
            }

            if (msg.type === 'SERVICE_STATUS_UPDATE' && msg.sender === 'System') {
                console.log("✅ Service Request Created:", msg.content.requestId);
                console.log("🎉 All Tests Passed!");
                socket.disconnect();
                process.exit(0);
            }
        });

        socket.on('connect_error', (err) => {
            console.error("❌ Socket Connection Error:", err.message);
            process.exit(1);
        });

    } catch (error) {
        console.error("❌ Test Failed:", error.message);
        if (error.response) {
            console.error("Response Data:", error.response.data);
        }
        process.exit(1);
    }
}

runTest();
