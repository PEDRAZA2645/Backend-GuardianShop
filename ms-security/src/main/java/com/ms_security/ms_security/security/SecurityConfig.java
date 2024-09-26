package com.ms_security.ms_security.security;

import com.ms_security.ms_security.service.IJWTUtilityService;
import com.ms_security.ms_security.service.impl.consultations.PermissionConsultations;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 * <p>
 * This class configures HTTP security and authentication, including:
 * <ul>
 *     <li>Configuration of security filters.</li>
 *     <li>Authorization rules for application routes.</li>
 *     <li>Session management policy.</li>
 *     <li>Handling of authentication-related exceptions.</li>
 * </ul>
 * </p>
 *
 * @see JWTAuthorizationFilter
 * @see IJWTUtilityService
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final IJWTUtilityService _jwtUtilityService;
    private final RoleConsultations _roleConsultations; // Asegúrate de tener esto
    private final PermissionConsultations _permissionConsultations; // Asegúrate de tener esto

    /**
     * Configures the security filter chain for HTTP.
     *
     * @param http The HTTP security configuration.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Crear una instancia del JWTAuthorizationFilter y pasarle las dependencias necesarias
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(
                _jwtUtilityService,
                _roleConsultations,
                _permissionConsultations
        );

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequests ->
                        authRequests
                                .requestMatchers("/auth/**", "/form/list/id", "/form/list/all", "/services/list/id", "/services/list/all").permitAll()
                                .requestMatchers("/permission/**", "/role/**", "/users/**")
                                .hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Añadir el filtro JWT antes del filtro de autenticación de usuario
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                .build();
    }

    /**
     * Configures the password encoder.
     *
     * @return The configured password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
