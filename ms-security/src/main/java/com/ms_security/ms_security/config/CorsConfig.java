package com.ms_security.ms_security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS).
 *
 * This class implements the WebMvcConfigurer interface to customize the
 * configuration of the Spring MVC framework. Specifically, it configures
 * CORS settings to control which domains are allowed to access the application
 * and which HTTP methods and headers are permitted.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings for the application.
     *
     * This method is used to define CORS policies by specifying which paths
     * should allow cross-origin requests, the allowed origins, HTTP methods,
     * headers, and other settings.
     *
     * - The first mapping applies to all paths ("**") and allows requests from
     *   "http://localhost:4200" with various methods like GET, POST, etc.
     *   Credentials are allowed, and the settings are cached for 3600 seconds.
     *
     * - The second mapping is specific to paths starting with "/auth/**" and
     *   follows similar CORS rules but does not allow credentials.
     *
     * @param registry the CORS registry to be configured
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Origin", "Content-type", "Accept", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/auth/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Origin", "Content-type", "Accept", "Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
