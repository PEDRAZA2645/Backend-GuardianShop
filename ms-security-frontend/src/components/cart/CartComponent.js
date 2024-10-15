import React, { useState, useEffect } from "react";
import axios from "axios";


const CartComponent = ({ cartItems }) => {
  const [message, setMessage] = useState("");

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
    } catch (error) {
      console.error("Error al eliminar el item", error);
      setMessage("Error al eliminar el item del carrito.");
    }
  };

  const totalAmount = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );

  return (
    <div className="cart">
      <h2>Tu Carrito</h2>
      {message && <p>{message}</p>}
      {cartItems.length === 0 ? (
        <p>No tienes productos en el carrito</p>
      ) : (
        <>
          <ul>
            {cartItems.map((item) => (
              <li key={item.id}>
                {item.name} - {item.quantity} unidades - $
                {item.price * item.quantity}
                <button onClick={() => handleRemoveItem(item.id)}>
                  Eliminar
                </button>
              </li>
            ))}
          </ul>
          <h3>Total: ${totalAmount.toFixed(2)}</h3>
          <button>Finalizar Compra</button>
        </>
      )}
    </div>
  );
};

export default CartComponent;
