package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for managing order services.
 * Provides methods for retrieving, adding, and updating orders.
 */
public interface IOrderService {

    ResponseEntity<String> findById(String encode);

    ResponseEntity<String> findAll(String encode);

    ResponseEntity<String> addNew(String encode);

    ResponseEntity<String> updateData(String encode);
}
