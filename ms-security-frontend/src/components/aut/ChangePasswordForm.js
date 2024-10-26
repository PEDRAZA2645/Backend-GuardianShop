import React, { useState } from 'react';
import { changePassword } from '../api';

const ChangePasswordForm = () => {
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await changePassword({ token, newPassword });
            alert('Password changed successfully.');
        } catch (error) {
            alert('Error changing password: ' + error.response.data.message);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Change Password</h2>
            <input
                type="text"
                placeholder="Token"
                value={token}
                onChange={(e) => setToken(e.target.value)}
            />
            <input
                type="password"
                placeholder="New Password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
            />
            <button type="submit">Change Password</button>
        </form>
    );
};

export default ChangePasswordForm;
