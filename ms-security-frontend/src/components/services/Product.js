import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ProductComponent = ({ onAddToCart }) => {
  const [services, setServices] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchServices = async () => {
      try {
        const payload = { page: 1 };
        const base64Payload = btoa(JSON.stringify(payload));

        const response = await fetch('http://localhost:8082/services/list/all', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: base64Payload,
        });

        if (!response.ok) {
          throw new Error('Network response was not ok');
        }

        const base64Data = await response.text();
        const jsonString = atob(base64Data);
        const data = JSON.parse(jsonString);

        setServices(data.data.content);
      } catch (error) {
        console.error('Error fetching services:', error);
        setError('Error fetching services');
      }
    };

    fetchServices();
  }, []);

  const handleCartIconClick = () => {
    navigate('/carts/list/all');
  };

  return (
    <div>
      <h1>Lista de Servicios</h1>
      <div style={{ position: 'relative' }}>
        <i 
          className="fa-solid fa-cart-shopping" 
          style={{
            position: 'absolute',
            top: '10px',
            right: '10px',
            fontSize: '24px',
            cursor: 'pointer',
          }} 
          onClick={handleCartIconClick} 
        />
      </div>
      {error && <p>{error}</p>}

      <ul>
        {services.map(service => (
          <li key={service.id}>
            <h2>{service.name}</h2>
            <p>Precio: ${service.salePrice ? service.salePrice : 'No disponible'}</p>
            <img src={service.imageUrl} alt={service.name} style={{ width: '100px', height: 'auto' }} />
            <button onClick={() => onAddToCart(service)}>Agregar al carrito</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ProductComponent;
