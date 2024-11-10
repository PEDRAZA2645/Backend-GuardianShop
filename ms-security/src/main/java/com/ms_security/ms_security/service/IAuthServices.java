package com.ms_security.ms_security.service;

import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.model.dto.ChangePasswordDto;
import com.ms_security.ms_security.service.model.dto.LoginDto;
import com.ms_security.ms_security.service.model.dto.ResponseErrorDto;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * Interface for authentication-related services.
 * <p>
 * Provides methods for user login, registration, password reset requests, and password changes.
 * </p>
 */
public interface IAuthServices {

    /**
     * Authenticates a user based on login credentials.
     *
     * @param login the login details (containing email and password)
     * @return a HashMap containing authentication tokens or relevant error messages
     * @throws Exception if authentication fails or an error occurs during the process
     */
    HashMap<String, String> login(LoginDto login) throws Exception;

    /**
     * Registers a new user by validating and saving their information.
     * <p>
     * This method validates the user's data, checks if the user already exists,
     * encodes the user's password, and saves the new user to the database.
     * </p>
     *
     * @param encode Base64 encoded string containing the new user details.
     * @return a ResponseDto with the result of the registration
     * @throws Exception if there is an error during registration
     */
    /**
     * Registers a new user by validating and saving their information.
     * <p>
     * This method validates the user's data, checks if the user already exists,
     * encodes the user's password, and saves the new user to the database.
     * </p>
     *
     * @param user the user entity to be registered
     * @return a ResponseDto with the result of the registration
     * @throws Exception if there is an error during registration
     */
    ResponseErrorDto register(UserEntity user) throws Exception;

    /**
     * Sends a password reset link to the user's email address.
     *
     * @param email the email address of the user requesting a password reset
     * @throws Exception if there is an error during the request process
     */
    void sendPasswordResetEmail(String email) throws Exception;

    /**
     * Resets the user's password using a provided token and new password.
     *
     * @param changePasswordDto the data transfer object containing the new password and token
     * @throws Exception if there is an error during the reset process
     */
    void changePassword(ChangePasswordDto changePasswordDto) throws Exception;

    void logout(String token);
}
