import React, { useState } from 'react';

const Stripe = () => {
  const [amount, setAmount] = useState('');

  const handlePayment = async () => {
    if (!amount) {
      alert('Please enter a valid amount.');
      return;
    }

    try {
      const response = await fetch('http://localhost:8082/stripe/paymentIntent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount: parseFloat(amount) * 100 }), // Stripe usa centavos
      });

      if (!response.ok) {
        throw new Error('Failed to create payment intent.');
      }

      const data = await response.json();
      alert(`Payment successful: ${data.id}`); // Ajusta esto según la respuesta de tu API
    } catch (error) {
      alert(`Payment failed. Please try again. Error: ${error.message}`);
    }
  };

  return (
    <div>
      <h2>Stripe</h2>
      <input
        type="number"
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />
      <button onClick={handlePayment}>Pay with Stripe</button>
    </div>
  );
};

export default Stripe;
