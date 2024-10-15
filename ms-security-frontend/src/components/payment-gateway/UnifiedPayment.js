import React, { useState } from 'react';

const UnifiedPayment = () => {
  const [method, setMethod] = useState('mercadopago');

  const handlePayment = async () => {
    try {
      const response = await fetch('http://localhost:8082/payment/pay', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          paymentMethod: method,
          description: 'Payment through unified endpoint',
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to initiate payment.');
      }

      const data = await response.json();

      // Redirigir al usuario según el método de pago
      switch (method) {
        case 'mercadopago':
        case 'paypal':
          const approvalUrl = data.approval_url || data.approvalUrl; // Ajusta según tu respuesta
          if (approvalUrl) {
            window.location.href = approvalUrl; // Redirige a la URL de aprobación
          } else {
            alert('Error: No se proporcionó una URL de aprobación.');
          }
          break;
        case 'stripe':
          alert(`Stripe payment initiated: ${data.client_secret}`); // Muestra el client_secret si lo necesitas
          break;
        default:
          alert('Invalid payment method selected.');
      }
    } catch (error) {
      alert(`Payment failed. Please try again. Error: ${error.message}`);
    }
  };

  return (
    <div>
      <h2>Unified Payment</h2>
      <select value={method} onChange={(e) => setMethod(e.target.value)}>
        <option value="mercadopago">MercadoPago</option>
        <option value="paypal">PayPal</option>
        <option value="stripe">Stripe</option>
      </select>
      <button onClick={handlePayment}>Pay</button>
    </div>
  );
};

export default UnifiedPayment;
