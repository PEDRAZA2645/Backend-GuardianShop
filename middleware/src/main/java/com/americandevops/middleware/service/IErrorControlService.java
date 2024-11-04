package com.americandevops.middleware.service;

import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import org.springframework.http.ResponseEntity;
import com.americandevops.middleware.service.model.ErrorControlDto;
import com.americandevops.middleware.service.model.FindByPageDto;

import java.util.List;

/**
 * Interface for managing Error Control services.
 */
public interface IErrorControlService {

    ResponseEntity<String> findById(String encode);

    ResponseEntity<String> findByErrorId(String encode);

    List<ErrorControlEntity>  findAll();

    ResponseEntity<String> findAll(String encode);

    ResponseEntity<String> addNew(String encode);

    ResponseEntity<String> updateData(String encode);
}

