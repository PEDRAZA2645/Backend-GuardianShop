package com.ms_security.ms_security.service;


import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.model.dto.LoginDto;
import com.ms_security.ms_security.service.model.dto.ResponseDto;

import java.util.HashMap;

/**
 * Interface for authentication-related services.
 * Provides methods for user login and registration.
 */
public interface IAuthServices {

    /**
     * Authenticates a user based on login credentials.
     *
     * @param login the login details (username and password)
     * @return a HashMap containing authentication tokens or relevant information
     * @throws Exception if authentication fails or an error occurs during the process
     */
    HashMap<String, String> login(LoginDto login) throws Exception;

    /**
     * Registers a new user in the system.
     *
     * @param user the user entity containing registration details
     * @return a ResponseDto containing information about the registration result
     * @throws Exception if registration fails or an error occurs during the process
     */
    ResponseDto register(UserEntity user) throws Exception;
}
