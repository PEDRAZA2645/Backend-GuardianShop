import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utilities/ConfigAxios';

const Auth = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [newPassword, setNewPassword] = useState(''); // Nuevo password
    const [token, setToken] = useState(''); // Token para restablecer contraseña
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            navigate('/services'); // Redirigir a la página de servicios si hay un token
        }
    }, [navigate]);

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post('/auth/login', {
                email,
                password,
            });

            console.log(response.data); // Verificar la respuesta completa

            if (response.status === 200) {
                // Almacenar el token de autenticación
                localStorage.setItem('token', response.data.jwt);
                console.log("Token almacenado:", response.data.jwt); // Verificar que se almacena
                setSuccessMessage("Inicio de sesión exitoso.");
                setErrorMessage('');
                navigate('/services'); // Redirigir a la página de servicios
            }
        } catch (error) {
            console.error("Error de inicio de sesión:", error); // Log de errores
            if (error.response && error.response.data) {
                setErrorMessage(error.response.data.message || "Error al iniciar sesión.");
            } else {
                setErrorMessage("Error de red. Inténtalo de nuevo.");
            }
            setSuccessMessage('');
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post('/auth/register', {
                name,
                email,
                password,
            });

            if (response.status === 201) {
                setSuccessMessage("Usuario registrado con éxito. Puedes iniciar sesión ahora.");
                setErrorMessage('');
                setIsLogin(true); // Cambiar a la vista de inicio de sesión
            }
        } catch (error) {
            if (error.response && error.response.data) {
                setErrorMessage(error.response.data.message || "Error al registrar el usuario.");
            } else {
                setErrorMessage("Error de red. Inténtalo de nuevo.");
            }
            setSuccessMessage('');
        }
    };

    const handlePasswordResetRequest = async (e) => {
        e.preventDefault();
        try {
            await api.post('/auth/reset-password', null, {
                params: { email }, // Envío del email
            });
            setSuccessMessage("Se ha enviado un correo para restablecer la contraseña.");
            setErrorMessage('');
        } catch (error) {
            if (error.response && error.response.data) {
                setErrorMessage(error.response.data.message || "Error al enviar el correo.");
            } else {
                setErrorMessage("Error de red. Inténtalo de nuevo.");
            }
        }
    };

    const handleChangePassword = async (e) => {
        e.preventDefault();
        try {
            await api.post('/auth/change-password', {
                token,
                newPassword, // El nuevo password
            });
            setSuccessMessage("Contraseña cambiada exitosamente.");
            setErrorMessage('');
            setNewPassword(''); // Limpiar el campo del nuevo password
            setToken(''); // Limpiar el token
            setIsLogin(true); // Volver a la vista de inicio de sesión
        } catch (error) {
            if (error.response && error.response.data) {
                setErrorMessage(error.response.data.message || "Error al cambiar la contraseña.");
            } else {
                setErrorMessage("Error de red. Inténtalo de nuevo.");
            }
        }
    };

    const handleLogout = async () => {
        try {
            const token = localStorage.getItem('token');
            await api.post('/auth/logout', {}, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            localStorage.removeItem('token');

            // Redirige a la página de inicio o donde desees
            navigate('/services');
            
            // Muestra un mensaje de éxito
            setSuccessMessage("Has cerrado sesión exitosamente.");
            setErrorMessage('');
        } catch (error) {
            // Maneja el error si ocurre
            if (error.response && error.response.data) {
                setErrorMessage(error.response.data.message || "Error al cerrar sesión.");
            } else {
                setErrorMessage("Error de red. Inténtalo de nuevo.");
            }
            setSuccessMessage('');
        }
    };
    

    return (
        <div>
            <h2>{isLogin ? "Inicio de Sesión" : "Registro de Usuario"}</h2>
            <form onSubmit={isLogin ? handleLogin : handleRegister}>
                {!isLogin && (
                    <div>
                        <label>Nombre:</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required={!isLogin}
                        />
                    </div>
                )}
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Contraseña:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">{isLogin ? "Iniciar Sesión" : "Registrar"}</button>
            </form>
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
            {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
            <button onClick={() => setIsLogin(!isLogin)}>
                {isLogin ? "¿No tienes una cuenta? Regístrate" : "¿Ya tienes una cuenta? Inicia sesión"}
            </button>

            {/* Sección para restablecimiento de contraseña */}
            {!isLogin && (
                <div>
                    <h2>Restablecer Contraseña</h2>
                    <form onSubmit={handlePasswordResetRequest}>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="Ingresa tu correo"
                        />
                        <button type="submit">Enviar correo para restablecer contraseña</button>
                    </form>

                    <form onSubmit={handleChangePassword}>
                        <input
                            type="text"
                            placeholder="Token"
                            value={token}
                            onChange={(e) => setToken(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Nueva Contraseña"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                        <button type="submit">Cambiar Contraseña</button>
                    </form>
                    {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
                    {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
                </div>
            )}

            {/* Botón de cierre de sesión */}
            <button onClick={handleLogout} style={{ marginTop: '20px' }}>Cerrar Sesión</button>
        </div>
    );
};

export default Auth;
