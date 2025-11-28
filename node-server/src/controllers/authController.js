const users = require('../data/users');

const register = (req, res) => {
    const { username, email, password, userType, firstName, lastName, phone, vehicleInfo } = req.body;

    if (users.find(u => u.username === username)) {
        return res.status(400).json({ success: false, message: "El usuario ya existe" });
    }

    if (users.find(u => u.email === email)) {
        return res.status(400).json({ success: false, message: "El email ya está registrado" });
    }

    const newUser = {
        id: users.length + 1,
        username,
        email,
        password, // In real app, hash this
        firstName,
        lastName,
        roles: [userType],
        isOnline: false,
        available: true,
        vehicleInfo: vehicleInfo || null,
        createdAt: new Date().toISOString()
    };

    users.push(newUser);

    res.status(200).json({
        success: true,
        message: "Usuario registrado exitosamente",
        username: newUser.username,
        userStateId: 1, // Mock state
        token: "mock-jwt-token-" + newUser.id
    });
};

const login = (req, res) => {
    const { username, password } = req.body;

    const user = users.find(u => u.username === username && u.password === password);

    if (!user) {
        return res.status(400).json({ success: false, message: "Credenciales inválidas" });
    }

    res.status(200).json({
        success: true,
        message: "Login exitoso",
        username: user.username,
        userStateId: 1, // Mock state
        token: "mock-jwt-token-" + user.id,
        timestamp: new Date().toISOString()
    });
};

const logout = (req, res) => {
    const { username } = req.params;
    // In a real app, invalidate token
    res.status(200).json({ success: true, message: "Logout exitoso" });
};

const verifyEmail = (req, res) => {
    // Mock verification
    res.status(200).json({ success: true, message: "Email verificado" });
};

module.exports = {
    register,
    login,
    logout,
    verifyEmail
};
