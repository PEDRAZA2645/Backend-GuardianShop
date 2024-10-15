import React, { useState } from 'react';

const PayPal = () => {
  const [amount, setAmount] = useState('');

  const handlePayment = async () => {
    if (!amount) {
      alert('Please enter a valid amount.');
      return;
    }

    try {
      const response = await fetch('http://localhost:8082/paypal/pay', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount: parseFloat(amount) }),
      });

      if (!response.ok) {
        throw new Error('Payment failed. Please try again.');
      }

      const data = await response.json();
      alert(`Payment successful: ${data}`); // Ajusta esto según la respuesta de tu API
    } catch (error) {
      alert(`Payment failed. Please try again. Error: ${error.message}`);
    }
  };

  return (
    <div>
      <h2>PayPal</h2>
      <input
        type="number"
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />
      <button onClick={handlePayment}>Pay with PayPal</button>
    </div>
  );
};

export default PayPal;
