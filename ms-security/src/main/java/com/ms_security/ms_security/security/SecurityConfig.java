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
 * <p>
 * The configuration performs the following actions:
 * <ul>
 *     <li>Disables CSRF protection.</li>
 *     <li>Configures authorization rules where:
 *         <ul>
 *             <li>Routes starting with "/auth/**", "/form/**", and "/services/**" are allowed without authentication.</li>
 *             <li>Routes starting with "/permission/**", "/role/**", and "/users/**" require the "ADMIN" role.</li>
 *             <li>All other requests must be authenticated.</li>
 *         </ul>
 *     </li>
 *     <li>Configures session management policy to be stateless.</li>
 *     <li>Adds the JWT authorization filter before the user authentication filter.</li>
 *     <li>Configures exception handling for unauthorized authentication responses.</li>
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

    /**
     * Configures the security filter chain for HTTP.
     *
     * @param http The HTTP security configuration.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequests ->
                        authRequests
                                .requestMatchers("/auth/**", "/form/**", "/services/**").permitAll()
                                .requestMatchers("/permission/**", "/role/**", "/users/**")
                                .hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JWTAuthorizationFilter(_jwtUtilityService),
                        UsernamePasswordAuthenticationFilter.class)
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
