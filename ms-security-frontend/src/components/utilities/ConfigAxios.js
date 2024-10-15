import axios from 'axios';

// Crear una instancia de Axios
const api = axios.create({
    baseURL: 'http://localhost:8082', // Cambia esto según tu API
});

// Interceptor para agregar el token en cada solicitud
api.interceptors.request.use(config => {
    // Verificar la ruta para omitir el token en login y register
    if (config.url === '/auth/login' || config.url === '/auth/register') {
        return config; // No se agrega el token
    }
    
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default api;
