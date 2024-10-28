import React, { useState, useEffect } from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import AuthComponent from './components/aut/auth';
import UnifiedPayment from './components/payment-gateway/UnifiedPayment';
import ServicesComponent from './components/services/Product';
import CartComponent from './components/cart/CartComponent';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; // Asegúrate de que jwt-decode está instalado

const App = () => {
    const [cartItems, setCartItems] = useState([]);
    const [userId, setUserId] = useState(null);
    const [createUser, setCreateUser] = useState('');
    const navigate = useNavigate();

    // Efecto para manejar el token JWT y obtener userId y createUser
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode(token); // Decodifica el JWT
                setUserId(decoded.userId);
                setCreateUser(decoded.createUser);
            } catch (error) {
                console.error("Error decoding token:", error);
                localStorage.removeItem('token'); // Limpia el token si hay error
                navigate('/auth'); // Redirige a login
            }
        }
    }, [navigate]);

    // Función para agregar productos al carrito
    const addToCart = async (service) => {
        const token = localStorage.getItem('token');

        // Verifica si el usuario está autenticado
        if (!token) {
            console.log('Usuario no autenticado, redirigiendo a login...');
            navigate('/auth');
            return; // Sale de la función si no hay token
        }

        try {
            // Busca si el producto ya está en el carrito
            const existingItem = cartItems.find(item => item.id === service.id);
            const updatedCartItems = existingItem
                ? cartItems.map(item => 
                    item.id === service.id 
                    ? { ...item, quantity: item.quantity + 1 }
                    : item
                )
                : [...cartItems, { ...service, quantity: 1 }];

            setCartItems(updatedCartItems);

            const cartId = 1; // Deberías obtener el cartId real aquí

            const cartData = {
                cartId: cartId,
                inventoryId: service.id,
                quantity: existingItem ? existingItem.quantity + 1 : 1,
                userId: userId,
                createUser: createUser,          
            };

            const base64Data = btoa(JSON.stringify(cartData)); // Encriptar los datos en base64
            await axios.post('http://localhost:8082/carts/addToCart', base64Data, {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Token JWT en el encabezado
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
                <Route path="/auth" element={<AuthComponent />} />
                <Route path="/payment" element={<UnifiedPayment />} />
                <Route path="/services" element={<ServicesComponent onAddToCart={addToCart} />} />
                <Route path="/carts" element={<CartComponent cartItems={cartItems} />} />
                <Route path="/carts/list/all" element={<CartComponent cartItems={cartItems} />} />
                <Route path="/carts/removeItem/:itemId" element={<CartComponent cartItems={cartItems} />} />
                <Route path="/" element={<ServicesComponent />} />
            </Routes>
        </div>
    );
};

export default App;
