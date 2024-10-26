import React, { useState } from 'react';
import { register } from '../api';

const RegisterForm = () => {
    const [user, setUser] = useState({
        name: '',
        lastName: '',
        email: '',
        password: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await register(user);
            alert('Registration successful');
        } catch (error) {
            alert('Registration failed: ' + error.response.data.message);
        }
    };

    const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Register</h2>
            <input
                type="text"
                name="name"
                placeholder="First Name"
                value={user.name}
                onChange={handleChange}
            />
            <input
                type="text"
                name="lastName"
                placeholder="Last Name"
                value={user.lastName}
                onChange={handleChange}
            />
            <input
                type="email"
                name="email"
                placeholder="Email"
                value={user.email}
                onChange={handleChange}
            />
            <input
                type="password"
                name="password"
                placeholder="Password"
                value={user.password}
                onChange={handleChange}
            />
            <button type="submit">Register</button>
        </form>
    );
};

export default RegisterForm;
