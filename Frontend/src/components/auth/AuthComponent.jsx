import { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import PropTypes from 'prop-types';

const AuthComponent = ({ mode }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    const tokenFromQuery = query.get('token');

    const [formData, setFormData] = useState({
        name: '',
        lastName: '',
        email: '',
        userName: '',
        status: 1,
        newPassword: '',
        confirmPassword: '',
        token: tokenFromQuery || '',
    });
    
    const [message, setMessage] = useState('');
    const [passwordsMatch, setPasswordsMatch] = useState(true);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const validatePasswords = () => {
        if ((mode === 'change-password' || mode === 'register') && formData.newPassword !== formData.confirmPassword) {
            setPasswordsMatch(false);
        } else {
            setPasswordsMatch(true);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let response;

            if (!passwordsMatch) {
                setMessage('Passwords do not match!');
                return;
            }

            switch (mode) {
                case 'login':
                    response = await axios.post('http://localhost:8082/auth/login', {
                        email: formData.email,
                        password: formData.newPassword, // Cambiado para usar newPassword
                    });
                    setMessage('Login successful!');
                    navigate('/dashboard');
                    break;

                case 'register':
                    response = await axios.post('http://localhost:8082/auth/register', {
                        name: formData.name,
                        lastName: formData.lastName,
                        userName: formData.userName,
                        email: formData.email,
                        password: formData.newPassword,
                        status: formData.status,
                    });
                    setMessage(response.data.message || 'Registration successful!');
                    navigate('/login');
                    break;

                case 'reset-password':
                    response = await axios.post('http://localhost:8082/auth/reset-password', {
                        email: formData.email,
                    });
                    setMessage(response.data.message || 'Password reset link sent to your email!');
                    break;

                case 'change-password':
                    response = await axios.post('http://localhost:8082/auth/change-password', {
                        newPassword: formData.newPassword,
                        token: formData.token,
                    });
                    setMessage(response.data.message || 'Password changed successfully!');
                    navigate('/login');
                    break;

                default:
                    break;
            }
        } catch (error) {
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
                        name="newPassword" // Cambiado de currentPassword a newPassword
                        placeholder="Password"
                        value={formData.newPassword}
                        onChange={handleInputChange}
                        onPaste={(e) => e.preventDefault()}
                        required
                    />
                )}
                {mode === 'change-password' && (
                    <>
                        <input
                            type="password"
                            name="newPassword"
                            placeholder="New Password"
                            value={formData.newPassword}
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
                {mode === 'register' && (
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
                            name="newPassword"
                            placeholder="Password"
                            value={formData.newPassword}
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
                )}
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
