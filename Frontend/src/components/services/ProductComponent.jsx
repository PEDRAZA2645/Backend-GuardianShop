import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import CartComponent from '../cart/CartComponent';
import './ProductComponent.css'; // Asegúrate de tener estilos para ProductComponent

const ProductComponent = ({ onAddToCart }) => {
  const [services, setServices] = useState([]);
  const [error, setError] = useState(null);
  const [showCart, setShowCart] = useState(false); // Estado para controlar la visualización del carrito
  const [formData, setFormData] = useState({
    name: '',
    lastName: '',
    userName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [message, setMessage] = useState('');
  const [passwordsMatch, setPasswordsMatch] = useState(true);

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
    setShowCart((prev) => !prev); // Alternar la visibilidad del carrito
  };

  // Maneja los cambios en los campos de entrada
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [name]: value }));
  };

  // Función para validar si las contraseñas coinciden
  const validatePasswords = () => {
    setPasswordsMatch(formData.password === formData.confirmPassword);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!passwordsMatch) {
      setMessage('Las contraseñas no coinciden.');
      return;
    }
    // Aquí puedes manejar el registro o el envío de datos del formulario
    setMessage('Formulario enviado con éxito!'); // Simulación de envío
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

      <form onSubmit={handleSubmit} className="auth-form" style={{ marginTop: '20px' }}>
        <input
          type="text"
          name="name"
          placeholder="Nombre"
          value={formData.name}
          onChange={handleInputChange}
          required
        />
        <input
          type="text"
          name="lastName"
          placeholder="Apellido"
          value={formData.lastName}
          onChange={handleInputChange}
          required
        />
        <input
          type="text"
          name="userName"
          placeholder="Nombre de usuario"
          value={formData.userName}
          onChange={handleInputChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Correo electrónico"
          value={formData.email}
          onChange={handleInputChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Contraseña"
          value={formData.password}
          onChange={handleInputChange}
          onBlur={validatePasswords}
          required
        />
        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirmar contraseña"
          value={formData.confirmPassword}
          onChange={handleInputChange}
          onBlur={validatePasswords}
          required
        />
        {!passwordsMatch && <p style={{ color: 'red' }}>Las contraseñas no coinciden!</p>}

        <button type="submit" className="auth-button">
          Enviar
        </button>
      </form>

      {message && <p>{message}</p>}
    </div>
  );
};

ProductComponent.propTypes = {
  onAddToCart: PropTypes.func.isRequired,
};

export default ProductComponent;
