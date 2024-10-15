package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.IAuthServices;
import com.ms_security.ms_security.service.IEmailService;
import com.ms_security.ms_security.service.IJWTUtilityService;
import com.ms_security.ms_security.service.impl.consultations.UserConsultations;
import com.ms_security.ms_security.service.model.dto.ChangePasswordDto;
import com.ms_security.ms_security.service.model.dto.EmailDto;
import com.ms_security.ms_security.service.model.dto.LoginDto;
import com.ms_security.ms_security.service.model.dto.ResponseErrorDto;
import com.ms_security.ms_security.service.model.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of the authentication services.
 * <p>
 * Provides methods for user login and registration. This service handles user authentication,
 * including password verification and JWT generation for authenticated users.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServicesImpl implements IAuthServices {

    private final UserConsultations _userConsultations;
    private final HashSet<String> _revokedTokens = new HashSet<>();
    private final IJWTUtilityService _jwtUtilityService;
    private final IEmailService _emailService;
    private final UserValidation _userValidation;

    /**
     * Logs in a user by validating their credentials and generating a JWT.
     * <p>
     * This method checks if the user exists, verifies the provided password,
     * and generates a JWT containing user roles and permissions.
     * </p>
     *
     * @param login the login DTO containing email and password
     * @return a HashMap containing the JWT or an error message
     * @throws Exception if there is an error during login
     */
    @Override
    public HashMap<String, String> login(LoginDto login) throws Exception {
        try {
            HashMap<String, String> jwt = new HashMap<>();
            Optional<UserEntity> user = _userConsultations.findByEmail(login.getEmail());
            if (user.isEmpty()) {
                jwt.put("Error", "User not registered!");
                return jwt;
            }
            if (verifyPassword(login.getPassword(), user.get().getPassword())) {
                Set<RoleEntity> roles = user.get().getRoles();
                Set<PermissionEntity> permissions = new HashSet<>();
                for (RoleEntity role : roles) {
                    permissions.addAll(role.getPermissions()); // Agrega permisos directamente
                }
                String token = _jwtUtilityService.generateJWT(user.get().getId(), roles, permissions);
                jwt.put("jwt", token);
            } else {
                jwt.put("Error", "Wrong password");
            }
            return jwt;
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }




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
    @Override
    public ResponseErrorDto register(UserEntity user) throws Exception {
        try {
            ResponseErrorDto response = _userValidation.validate(user);
            if (response.getNumOfErrors() > 0) {
                return response;
            }
            Optional<UserEntity> existingUser = _userConsultations.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                response.setNumOfErrors(1);
                response.setMessage("User already exists!");
                return response;
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            user.setPassword(encoder.encode(user.getPassword()));
            _userConsultations.addNew(user);
            response.setMessage("User successfully registered!");
            return response;
        } catch (Exception e) {
            throw new Exception("Error registering user: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies if the entered password matches the stored password.
     * <p>
     * This method compares the entered password with the stored hashed password
     * to determine if they match.
     * </p>
     *
     * @param enteredPassword the password entered by the user
     * @param storedPassword  the stored hashed password
     * @return true if the passwords match, false otherwise
     */
    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        return new BCryptPasswordEncoder().matches(enteredPassword, storedPassword);
    }
    @Override
    public void sendPasswordResetEmail(String email) throws Exception {
        Optional<UserEntity> user = _userConsultations.findByEmail(email);
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }
        String token = generateResetToken(user.get());
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        EmailDto emailDto = new EmailDto();
        emailDto.setRecipient(email);
        emailDto.setSubject("Password Reset Request");
        emailDto.setMessage("Click the link to reset your password: " + resetLink);

        _emailService.sendEmail(emailDto);
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) throws Exception {
        Optional<UserEntity> user = getUserFromToken(changePasswordDto.getToken());
        if (user.isPresent()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            user.get().setPassword(encoder.encode(changePasswordDto.getNewPassword()));
            _userConsultations.updateData(user.get());
        } else {
            throw new Exception("Invalid token");
        }
    }

    private String generateResetToken(UserEntity user) {
        return UUID.randomUUID().toString();
    }

    private Optional<UserEntity> getUserFromToken(String token) {
        return Optional.empty(); // Implementa la lógica real
    }

    /**
     * Logs out the user by revoking the provided token.
     * <p>
     * This method adds the token to the revoked tokens set, effectively invalidating it.
     * </p>
     *
     * @param token the JWT token to be revoked
     */
    @Override
    public void logout(String token) {
        _revokedTokens.add(token);
    }
}
