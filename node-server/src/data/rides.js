const rides = [
    {
        id: 1,
        requestId: "REQ-123456",
        riderId: 2,
        riderName: "rider",
        driverId: 3,
        driverName: "driver",
        pickupAddress: "Central Park, NY",
        destinationAddress: "Times Square, NY",
        status: "COMPLETED",
        requestedAt: "2023-10-26T10:00:00",
        completedAt: "2023-10-26T10:30:00",
        estimatedPrice: 25.0,
        finalPrice: 25.0
    },
    {
        id: 2,
        requestId: "REQ-789012",
        riderId: 2,
        riderName: "rider",
        driverId: 5,
        driverName: "driver2",
        pickupAddress: "JFK Airport",
        destinationAddress: "Brooklyn Bridge",
        status: "CANCELLED",
        requestedAt: "2023-10-27T14:00:00",
        cancelledAt: "2023-10-27T14:15:00",
        estimatedPrice: 45.0,
        finalPrice: 0.0
    }
];

module.exports = rides;
