import React, { useState } from 'react';
import axios from 'axios';

const AuthComponent = () => {
    const [form, setForm] = useState('login');
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        token: ''
    });
    const [message, setMessage] = useState('');
    const [jwtToken, setJwtToken] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let response;

            switch (form) {
                case 'login':
                    response = await axios.post('http://localhost:8082/auth/login', {
                        email: formData.email,
                        password: formData.password
                    });
                    setJwtToken(response.data.jwt);
                    setMessage('Login successful!');
                    break;
                
                case 'register':
                    response = await axios.post('http://localhost:8082/auth/register', {
                        email: formData.email,
                        password: formData.password,
                        // otros datos necesarios para el registro
                    });
                    setMessage(response.data.message || 'Registration successful!');
                    break;

                case 'reset-password':
                    response = await axios.post('http://localhost:8082/auth/reset-password', null, {
                        params: { email: formData.email }
                    });
                    setMessage(response.data || 'Password reset email sent.');
                    break;

                case 'change-password':
                    response = await axios.post('http://localhost:8082/auth/change-password', {
                        newPassword: formData.password,
                        token: formData.token
                    });
                    setMessage(response.data || 'Password changed successfully!');
                    break;

                default:
                    break;
            }
        } catch (error) {
            setMessage(error.response?.data?.message || 'An error occurred');
        }
    };

    const handleLogout = async () => {
        try {
            await axios.post(
                'http://localhost:8082/auth/logout',
                {},
                {
                    headers: { Authorization: `Bearer ${jwtToken}` }
                }
            );
            setJwtToken(null);
            setMessage('Logged out successfully!');
        } catch (error) {
            setMessage(error.response?.data?.message || 'Logout failed');
        }
    };

    return (
        <div>
            <h2>Authentication</h2>
            <div>
                <button onClick={() => setForm('login')}>Login</button>
                <button onClick={() => setForm('register')}>Register</button>
                <button onClick={() => setForm('reset-password')}>Reset Password</button>
                <button onClick={() => setForm('change-password')}>Change Password</button>
            </div>

            {jwtToken && <button onClick={handleLogout}>Logout</button>}

            <form onSubmit={handleSubmit}>
                {(form === 'login' || form === 'register') && (
                    <>
                        <input
                            type="email"
                            name="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                        />
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                        />
                    </>
                )}

                {form === 'register' && (
                    <input
                        type="password"
                        name="confirmPassword"
                        placeholder="Confirm Password"
                        value={formData.confirmPassword}
                        onChange={handleInputChange}
                        required
                    />
                )}

                {form === 'reset-password' && (
                    <input
                        type="email"
                        name="email"
                        placeholder="Email for password reset"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                    />
                )}

                {form === 'change-password' && (
                    <>
                        <input
                            type="password"
                            name="password"
                            placeholder="New Password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                        />
                        <input
                            type="text"
                            name="token"
                            placeholder="Token"
                            value={formData.token}
                            onChange={handleInputChange}
                            required
                        />
                    </>
                )}

                <button type="submit">{form === 'login' ? 'Login' : 'Submit'}</button>
            </form>

            {message && <p>{message}</p>}
        </div>
    );
};

export default AuthComponent;
