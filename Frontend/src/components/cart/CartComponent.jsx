import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";

const CartComponent = ({ onRemoveFromCart }) => {
    const [cartItems, setCartItems] = useState([]);
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newCart, setNewCart] = useState({ createUser: '', status: 'PENDING' });
    const [updatedCart, setUpdatedCart] = useState({ id: '', updateUser: '', status: 'PENDING' });
    const [itemToAdd, setItemToAdd] = useState({ cartId: '', itemId: '', quantity: 1 });
    const [cartIdToFetch, setCartIdToFetch] = useState('');
    const [fetchedCart, setFetchedCart] = useState(null);

    useEffect(() => {
        const fetchCarts = async () => {
            try {
                const token = localStorage.getItem("token");
                const response = await axios.post("http://localhost:8082/carts/list/all", '', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setCartItems(response.data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCarts();
    }, []);

    // Fetch specific cart by ID
    const handleFetchCart = async () => {
        try {
            const token = localStorage.getItem("token");
            const base64Data = btoa(JSON.stringify({ id: cartIdToFetch }));
            const response = await axios.post(`http://localhost:8082/carts/list/${cartIdToFetch}`, base64Data, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setFetchedCart(response.data);
            setMessage(`Carrito con ID ${cartIdToFetch} obtenido.`);
        } catch (err) {
            console.error("Error al obtener el carrito", err);
            setMessage("Error al obtener el carrito.");
        }
    };

    // Add new cart
    const handleAddNewCart = async () => {
        try {
            const token = localStorage.getItem("token");
            const base64Data = btoa(JSON.stringify(newCart));
            await axios.post("http://localhost:8082/carts/addRecord", base64Data, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage("Nuevo carrito agregado.");
            setNewCart({ createUser: '', status: 'PENDING' });
        } catch (err) {
            console.error("Error al agregar el carrito", err);
            setMessage("Error al agregar el carrito.");
        }
    };

    // Update existing cart
    const handleUpdateCart = async () => {
        try {
            const token = localStorage.getItem("token");
            const base64Data = btoa(JSON.stringify(updatedCart));
            await axios.post("http://localhost:8082/carts/updateRecord", base64Data, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage("Carrito actualizado.");
            setUpdatedCart({ id: '', updateUser: '', status: 'PENDING' });
        } catch (err) {
            console.error("Error al actualizar el carrito", err);
            setMessage("Error al actualizar el carrito.");
        }
    };

    // Add item to cart
    const handleAddToCart = async () => {
        try {
            const token = localStorage.getItem("token");
            const base64Data = btoa(JSON.stringify(itemToAdd));
            await axios.post("http://localhost:8082/carts/addToCart", base64Data, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage("Item agregado al carrito.");
            setItemToAdd({ cartId: '', itemId: '', quantity: 1 });
        } catch (err) {
            console.error("Error al agregar el item al carrito", err);
            setMessage("Error al agregar el item al carrito.");
        }
    };

    // Remove item from cart
    const handleRemoveItem = async (itemId) => {
        try {
            const base64Data = btoa(JSON.stringify({ id: 1, items: [{ id: itemId }] }));
            const token = localStorage.getItem("token");
            await axios.post("http://localhost:8082/carts/removeItem", base64Data, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage(`Eliminado el item con ID ${itemId} del carrito.`);
            onRemoveFromCart(itemId); // Llamar a la función pasada desde ProductComponent
        } catch (error) {
            console.error("Error al eliminar el item", error);
            setMessage("Error al eliminar el item del carrito.");
        }
    };

    // Delete cart
    const handleDeleteCart = async (cartId) => {
        try {
            const token = localStorage.getItem("token");
            const base64Data = btoa(JSON.stringify({ id: cartId }));
            await axios.post("http://localhost:8082/carts/deleteCart", base64Data, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage(`Carrito con ID ${cartId} eliminado.`);
        } catch (error) {
            console.error("Error al eliminar el carrito", error);
            setMessage("Error al eliminar el carrito.");
        }
    };

    if (loading) return <p>Cargando...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="cart">
            <h2>Tu Carrito</h2>
            {message && <p>{message}</p>}

            {/* Formulario para obtener un carrito específico */}
            <h3>Obtener Carrito por ID</h3>
            <input
                type="text"
                placeholder="ID del Carrito"
                value={cartIdToFetch}
                onChange={e => setCartIdToFetch(e.target.value)}
            />
            <button onClick={handleFetchCart}>Obtener Carrito</button>
            {fetchedCart && (
                <div>
                    <h4>Detalles del Carrito</h4>
                    {/* Renderiza detalles del carrito aquí */}
                    <pre>{JSON.stringify(fetchedCart, null, 2)}</pre>
                </div>
            )}

            {/* Sección para agregar un nuevo carrito */}
            <h3>Agregar Nuevo Carrito</h3>
            <input
                type="text"
                placeholder="Creador"
                value={newCart.createUser}
                onChange={e => setNewCart(prev => ({ ...prev, createUser: e.target.value }))}
            />
            <button onClick={handleAddNewCart}>Agregar Carrito</button>

            {/* Sección para actualizar un carrito existente */}
            <h3>Actualizar Carrito</h3>
            <input
                type="text"
                placeholder="ID del Carrito"
                value={updatedCart.id}
                onChange={e => setUpdatedCart(prev => ({ ...prev, id: e.target.value }))}
            />
            <input
                type="text"
                placeholder="Actualizador"
                value={updatedCart.updateUser}
                onChange={e => setUpdatedCart(prev => ({ ...prev, updateUser: e.target.value }))}
            />
            <button onClick={handleUpdateCart}>Actualizar Carrito</button>

            {/* Sección para agregar items al carrito */}
            <h3>Agregar Item al Carrito</h3>
            <input
                type="text"
                placeholder="ID del Carrito"
                value={itemToAdd.cartId}
                onChange={e => setItemToAdd(prev => ({ ...prev, cartId: e.target.value }))}
            />
            <input
                type="text"
                placeholder="ID del Item"
                value={itemToAdd.itemId}
                onChange={e => setItemToAdd(prev => ({ ...prev, itemId: e.target.value }))}
            />
            <input
                type="number"
                placeholder="Cantidad"
                value={itemToAdd.quantity}
                onChange={e => setItemToAdd(prev => ({ ...prev, quantity: Number(e.target.value) }))}
            />
            <button onClick={handleAddToCart}>Agregar Item</button>

            {/* Renderiza los items del carrito */}
            <ul>
                {cartItems.map((item) => (
                    <li key={item.id}>
                        <h4>{item.name}</h4>
                        <button onClick={() => handleRemoveItem(item.id)}>Eliminar Item</button>
                        <button onClick={() => handleDeleteCart(item.id)}>Eliminar Carrito</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

CartComponent.propTypes = {
    onRemoveFromCart: PropTypes.func.isRequired,
};

export default CartComponent;
