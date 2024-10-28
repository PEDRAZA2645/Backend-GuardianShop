import { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import PropTypes from 'prop-types';

const AuthComponent = ({ mode }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    const tokenFromQuery = query.get('token');

    // Estado para almacenar los datos del formulario
    const [formData, setFormData] = useState({
        name: '',
        lastName: '',
        email: '',
        userName: '',
        status: 1,
        currentPassword: '',
        password: '',
        confirmPassword: '',
        token: tokenFromQuery || '',
    });

    // Estado para mensajes y validación de contraseñas
    const [message, setMessage] = useState('');
    const [passwordsMatch, setPasswordsMatch] = useState(true);

    // Maneja los cambios en los campos de entrada
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    // Función para validar si las contraseñas coinciden
    const validatePasswords = () => {
        if ((mode === 'change-password' || mode === 'register') && formData.password !== formData.confirmPassword) {
            setPasswordsMatch(false);
        } else {
            setPasswordsMatch(true);
        }
    };

    // Maneja el envío del formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let response;

            // Verifica si las contraseñas coinciden antes de proceder
            if (!passwordsMatch) {
                setMessage('Passwords do not match!');
                return;
            }

            console.log('Datos a enviar:', {
                name: formData.name,
                lastName: formData.lastName,
                userName: formData.userName,
                email: formData.email,
                password: formData.password,
                status: formData.status ? 1 : 0,
            });

            switch (mode) {
                case 'login':
                    // Maneja la lógica de inicio de sesión
                    response = await axios.post('http://localhost:8082/auth/login', {
                        email: formData.email,
                        password: formData.currentPassword,
                    });
                    setMessage('Login successful!');
                    navigate('/');
                    break;

                case 'register': {
                    // Datos de registro para el backend
                    const registerData = {
                        id: null,
                        name: formData.name,
                        lastName: formData.lastName,
                        userName: formData.userName,
                        email: formData.email,
                        password: formData.password,
                        status: true,
                        createUser: formData.name,
                        updateUser: null,
                        rolesToAdd: null,
                        roles: [],
                    };

                    // Codificación a Base64
                    const jsonString = JSON.stringify(registerData);
                    const encodedRegisterData = btoa(unescape(encodeURIComponent(jsonString)));
                    console.log('Encoded Register Data (Base64):', encodedRegisterData);

                    // Envío de datos al servidor
                    response = await axios.post('http://localhost:8082/auth/register', {
                        data: encodedRegisterData,
                    }, {
                        headers: {
                            'Content-Type': 'application/json',
                        },
                    });

                    setMessage(response.data.message || 'Registration successful!');
                    navigate('/login');
                    break;
                }

                case 'reset-password':
                    // Maneja la lógica para restablecer la contraseña
                    response = await axios.post('http://localhost:8082/auth/reset-password', {
                        email: formData.email,
                    });
                    setMessage(response.data.message || 'Password reset link sent to your email!');
                    break;

                case 'change-password':
                    // Maneja la lógica para cambiar la contraseña
                    response = await axios.post('http://localhost:8082/auth/change-password', {
                        currentPassword: formData.currentPassword,
                        newPassword: formData.password,
                        token: formData.token,
                    });
                    setMessage(response.data.message || 'Password changed successfully!');
                    navigate('/login');
                    break;

                default:
                    break;
            }
        } catch (error) {
            // Manejo de errores
            console.error('Error details:', error);
            setMessage(error.response?.data?.message || 'An error occurred');
        }
    };

    return (
        <div className="auth-container">
            <h2 className="auth-title">
                {mode === 'login' ? 'Login' : mode === 'register' ? 'Register' : mode === 'reset-password' ? 'Reset Password' : 'Change Password'}
            </h2>

            <form onSubmit={handleSubmit} className="auth-form">
                {(mode === 'login' || mode === 'register' || mode === 'reset-password') && (
                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleInputChange}
                        onPaste={(e) => e.preventDefault()}
                        required
                    />
                )}
                {mode === 'login' && (
                    <input
                        type="password"
                        name="currentPassword"
                        placeholder="Password"
                        value={formData.currentPassword}
                        onChange={handleInputChange}
                        onPaste={(e) => e.preventDefault()}
                        required
                    />
                )}
                {mode === 'change-password' && (
                    <>
                        <input
                            type="password"
                            name="currentPassword"
                            placeholder="Current Password"
                            value={formData.currentPassword}
                            onChange={handleInputChange}
                            onPaste={(e) => e.preventDefault()}
                            required
                        />
                        <input
                            type="password"
                            name="newPassword"
                            placeholder="New Password"
                            value={formData.password}
                            onChange={handleInputChange}
                            onBlur={validatePasswords}
                            onPaste={(e) => e.preventDefault()}
                            required
                        />
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm New Password"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            onBlur={validatePasswords}
                            onPaste={(e) => e.preventDefault()}
                            required
                        />
                    </>
                )}
                {/* {mode === 'register' && (
                    <>
                        <input
                            type="text"
                            name="name"
                            placeholder="First Name"
                            value={formData.name}
                            onChange={handleInputChange}
                            required
                        />
                        <input
                            type="text"
                            name="lastName"
                            placeholder="Last Name"
                            value={formData.lastName}
                            onChange={handleInputChange}
                            required
                        />
                        <input
                            type="text"
                            name="userName"
                            placeholder="Username"
                            value={formData.userName}
                            onChange={handleInputChange}
                            required
                        />
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleInputChange}
                            onBlur={validatePasswords}
                            required
                        />
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            onBlur={validatePasswords}
                            required
                        />
                    </>
                )} */}
                {!passwordsMatch && <p style={{ color: 'red' }}>Passwords do not match!</p>}
                
                <button type="submit" className="auth-button">
                    {mode === 'login' ? 'Login' : mode === 'register' ? 'Register' : mode === 'reset-password' ? 'Send Reset Link' : 'Change Password'}
                </button>
            </form>

            {message && <p>{message}</p>}

            {mode === 'login' && (
                <div>
                    <button onClick={() => navigate('/register')} className="auth-link">Do not have an account? Register</button>
                    <button onClick={() => navigate('/reset-password')} className="auth-link">Forgot Password? Reset</button>
                </div>
            )}
        </div>
    );
};

// Validación de propiedades
AuthComponent.propTypes = {
    mode: PropTypes.oneOf(['login', 'register', 'reset-password', 'change-password']).isRequired,
};

export default AuthComponent;
