import axios from 'axios';

const API_URL = 'http://localhost:8080/auth'; // URL de tu backend

// Función para registrar un usuario
export const register = async (user) => {
    return await axios.post(`${API_URL}/register`, user);
};

// Función para iniciar sesión
export const login = async (credentials) => {
    return await axios.post(`${API_URL}/login`, credentials);
};

// Función para enviar el correo de restablecimiento de contraseña
export const resetPassword = async (email) => {
    return await axios.post(`${API_URL}/reset-password`, null, { params: { email } });
};

// Función para cambiar la contraseña
export const changePassword = async (data) => {
    return await axios.post(`${API_URL}/change-password`, data);
};

// Función para cerrar sesión
export const logout = async (token) => {
    return await axios.post(`${API_URL}/logout`, {}, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
};
