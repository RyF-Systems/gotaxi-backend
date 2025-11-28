const users = [
    {
        id: 1,
        username: "admin",
        email: "admin@ryftaxi.com",
        password: "password123", // In a real app, this should be hashed
        firstName: "Admin",
        lastName: "User",
        roles: [1, 2],
        isOnline: false,
        available: true
    },
    {
        id: 2,
        username: "rider",
        email: "rider@ryftaxi.com",
        password: "password123",
        firstName: "Rider",
        lastName: "One",
        roles: [4], // Rider role
        isOnline: false,
        available: true
    },
    {
        id: 3,
        username: "driver",
        email: "driver@ryftaxi.com",
        password: "password123",
        firstName: "Driver",
        lastName: "One",
        roles: [3], // Driver role
        isOnline: false,
        available: true,
        vehicleInfo: {
            brand: "Toyota",
            model: "Corolla",
            plate: "ABC-123"
        }
    },
    {
        id: 4,
        username: "rider2",
        email: "rider2@ryftaxi.com",
        password: "password123",
        firstName: "Rider",
        lastName: "Two",
        roles: [4],
        isOnline: false,
        available: true
    },
    {
        id: 5,
        username: "driver2",
        email: "driver2@ryftaxi.com",
        password: "password123",
        firstName: "Driver",
        lastName: "Two",
        roles: [3],
        isOnline: false,
        available: true,
        vehicleInfo: {
            brand: "Honda",
            model: "Civic",
            plate: "XYZ-789"
        }
    }
];

module.exports = users;
