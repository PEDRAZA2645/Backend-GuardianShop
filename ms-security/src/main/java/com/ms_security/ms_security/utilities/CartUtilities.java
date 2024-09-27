package com.ms_security.ms_security.utilities;

import com.ms_security.ms_security.persistence.entity.CartEntity;
import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.OrderItemEntity;

public class CartUtilities {

    public static void addItemToCart(CartEntity cart, InventoryEntity inventory, Long quantity) {
        boolean itemExists = false;

        for (OrderItemEntity item : cart.getItems()) {
            if (item.getProduct().getId().equals(inventory.getId())) {
                item.setQuantity(item.getQuantity() + quantity); // Incrementar la cantidad existente
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            OrderItemEntity newItem = new OrderItemEntity();
            newItem.setProduct(inventory);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem); // Agregar el nuevo artículo al carrito
        }
    }

    public static boolean removeItemFromCart(CartEntity cart, Long inventoryId) {
        if (cart.getItems() != null) {
            // Buscar el ítem en la lista y eliminarlo
            return cart.getItems().removeIf(item -> item.getProduct().getId().equals(inventoryId));
        }
        return false;
    }

}
