import React, { useState } from 'react';
import { resetPassword } from '../api';

const ResetPasswordForm = () => {
    const [email, setEmail] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await resetPassword(email);
            alert('Password reset email sent.');
        } catch (error) {
            alert('Error sending email: ' + error.response.data.message);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Reset Password</h2>
            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <button type="submit">Send Reset Email</button>
        </form>
    );
};

export default ResetPasswordForm;
