import React, { useState, useEffect } from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import Login from './components/aut/auth'; 
import UnifiedPayment from './components/payment-gateway/UnifiedPayment';
import ServicesComponent from './components/services/Product';
import CartComponent from './components/cart/CartComponent';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const App = () => {
  const [cartItems, setCartItems] = useState([]);
  const [userId, setUserId] = useState(null);
  const [createUser, setCreateUser] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUserId(decoded.userId);
        setCreateUser(decoded.createUser);
      } catch (error) {
        console.error("Error decoding token:", error);
        localStorage.removeItem('token'); // Limpiar el token si hay un error
        navigate('/auth'); // Redirigir al login
      }
    }
  }, [navigate]);

  const addToCart = async (service) => {
    const token = localStorage.getItem('token');

    // Verificar si el usuario está logueado
    if (!token) {
      console.log('Usuario no autenticado, redirigiendo a login...');
      navigate('/auth'); // Redirigir a iniciar sesión
      return; // Asegúrate de salir de la función
    }

    try {
      // Verificar si el producto ya existe en el carrito
      const existingItem = cartItems.find(item => item.id === service.id);
      const updatedCartItems = existingItem
        ? cartItems.map(item => 
            item.id === service.id 
            ? { ...item, quantity: item.quantity + 1 }
            : item
          )
        : [...cartItems, { ...service, quantity: 1 }];

      setCartItems(updatedCartItems);

      // Aquí deberías obtener el cartId real en lugar de usar un valor fijo
      const cartId = 1; // Cambia esto según tu lógica para obtener el cartId

      const cartData = {
        cartId: cartId,
        inventoryId: service.id,
        quantity: existingItem ? existingItem.quantity + 1 : 1,
        userId: userId,
        createUser: createUser,
      };

      const base64Data = btoa(JSON.stringify(cartData));
      await axios.post('http://localhost:8082/carts/addToCart', base64Data, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      console.log('Producto agregado al carrito:', cartData);
    } catch (error) {
      console.error('Error al agregar al carrito:', error.response ? error.response.data : error.message);
    }
  };

  return (
    <div className="App">
      <h1>Payment Integration Demo</h1>
      <Routes>
        <Route path="/auth" element={<Login />} />
        <Route path="/payment" element={<UnifiedPayment />} />
        <Route path="/services" element={<ServicesComponent onAddToCart={addToCart} />} />
        <Route path="/carts" element={<CartComponent cartItems={cartItems} />} />
        <Route path="/carts/list/all" element={<CartComponent cartItems={cartItems} />} />
        <Route path="/carts/removeItem/:itemId" element={<CartComponent cartItems={cartItems} />} />
        <Route path="/" element={<Login />} />
      </Routes>
    </div>
  );
};

export default App;
