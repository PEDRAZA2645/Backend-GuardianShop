import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import AuthComponent from './components/auth/AuthComponent';
import CartComponent from './components/cart/CartComponent';
import ProductComponent from './components/services/ProductComponent';
import { useState } from 'react';

const App = () => {
    const [cartItems, setCartItems] = useState([]); // Aquí defines el estado del carrito

    // Función para agregar un item al carrito
    const handleAddToCart = (item) => {
        setCartItems((prevItems) => [...prevItems, item]);
    };

    // Función para eliminar un item del carrito
    const handleRemoveFromCart = (itemId) => {
        setCartItems((prevItems) => prevItems.filter(item => item.id !== itemId));
    };

    return (
        <Router>
            <div>
                <Routes>
                    <Route path="/" element={<Navigate to="/services" replace />} />
                    <Route path="/login" element={<AuthComponent mode="login" />} />
                    <Route path="/register" element={<AuthComponent mode="register" />} />
                    <Route path="/reset-password" element={<AuthComponent mode="reset-password" />} />
                    <Route path="/change-password" element={<AuthComponent mode="change-password" />} />
                    <Route path="/services" element={<ProductComponent onAddToCart={handleAddToCart} />} />
                    <Route path="/carts/list/all" element={<CartComponent cartItems={cartItems} onRemoveFromCart={handleRemoveFromCart} />} />
                    <Route path="*" element={<h2>404 - Page Not Found</h2>} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;
