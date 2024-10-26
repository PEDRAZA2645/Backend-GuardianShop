import React from 'react';
import { logout } from '../api';

const LogoutButton = ({ token, clearToken }) => {
    const handleLogout = async () => {
        try {
            await logout(token);
            clearToken();
            alert('Logout successful');
        } catch (error) {
            alert('Error logging out: ' + error.response.data.message);
        }
    };

    return (
        <button onClick={handleLogout}>Logout</button>
    );
};

export default LogoutButton;
