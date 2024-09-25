package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.IAuthServices;
import com.ms_security.ms_security.service.model.dto.LoginDto;
import com.ms_security.ms_security.service.model.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Controller class for handling authentication-related operations.
 * This class provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthServices _authServices;

    /**
     * Registers a new user.
     *
     * @param user the user entity containing the details of the new user
     * @return a ResponseEntity containing a ResponseDto with the registration status
     * @throws Exception if an error occurs during the registration process
     */
    @Operation(summary = "Register a new user", description = "Registers a new user and returns the registration status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/register")
    private ResponseEntity<ResponseDto> register(@RequestBody UserEntity user) throws Exception {
        return new ResponseEntity<>(_authServices.register(user), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login DTO containing the user's credentials
     * @return a ResponseEntity containing a HashMap with the JWT token or an error message
     * @throws Exception if an error occurs during the authentication process
     */
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content(schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = HashMap.class)))
    })
    @PostMapping("/login")
    private ResponseEntity<HashMap<String, String>> login(@RequestBody LoginDto loginRequest) throws Exception {
        HashMap<String, String> login = _authServices.login(loginRequest);
        if (login.containsKey("jwt")) {
            return new ResponseEntity<>(login, HttpStatus.OK);
        }
        return new ResponseEntity<>(login, HttpStatus.UNAUTHORIZED);
    }
}
