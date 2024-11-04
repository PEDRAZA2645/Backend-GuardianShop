import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import CartComponent from '../cart/CartComponent';
import './ProductComponent.css'; // Asegúrate de tener estilos para ProductComponent

const ProductComponent = ({ onAddToCart }) => {
  const [services, setServices] = useState([]);
  const [error, setError] = useState(null);
  const [showCart, setShowCart] = useState(false); // Estado para controlar la visualización del carrito

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
    setShowCart(prev => !prev); // Alternar la visibilidad del carrito
  };

  return (
    <div>
      <h1>Lista de Productos</h1>
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

      <div className="container">
        <div className="row d-flex flex-wrap row-cols-1 row-cols-md-2 row-cols-lg-3">
          {services.map((service) => (
            <div key={service.id} className="col mb-4">
              <div className="card" style={{ width: '18rem' }}>
                <img src={service.imageUrl} className="card-img-top" alt={service.name} />
                <div className="card-body">
                  <h5 className="card-title">{service.name}</h5>
                  <p className="card-text">
                    Precio: ${service.salePrice ? service.salePrice : 'No disponible'}
                  </p>
                  <button className="btn btn-primary" onClick={() => onAddToCart(service)}>
                    Agregar al carrito
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {showCart && <CartComponent />} {/* Mostrar el carrito si showCart es true */}
    </div>
  );
};

ProductComponent.propTypes = {
  onAddToCart: PropTypes.func.isRequired,
};

export default ProductComponent;