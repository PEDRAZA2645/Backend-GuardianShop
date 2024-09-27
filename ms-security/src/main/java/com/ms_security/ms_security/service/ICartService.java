package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for managing cart services.
 * Provides methods for adding items to the cart and checking out.
 */
public interface ICartService {

    ResponseEntity<String> findById(String encode);

    ResponseEntity<String> findAll(String encode);

    ResponseEntity<String> addNew(String encode);

    ResponseEntity<String> updateData(String encode);

    ResponseEntity<String> addToCart(String cartIdBase64, String inventoryIdBase64, String quantityBase64);

    ResponseEntity<String> removeFromCart(String cartIdBase64, String inventoryIdBase64);

    ResponseEntity<String> deleteCart(String encode);

    ResponseEntity<String> checkout(String cartIdBase64);
}
