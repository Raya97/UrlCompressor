package com.lioness.urlcompressor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig sets up the main Spring Security configuration for the application.
 * It defines route-level authorization rules, integrates the JWT authentication filter,
 * and enables method-level access control via @PreAuthorize.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // Custom JWT authentication filter
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the security filter chain, including route permissions,
     * CSRF settings, exception handling, and custom filters.
     *
     * @param http the HttpSecurity object
     * @return the configured SecurityFilterChain
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF for stateless JWT-based API
                .csrf(csrf -> csrf.disable())

                // Define access rules for different URL patterns
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/user/signin", "/api/v1/user/signup",
                                "/api/v1/user/refresh", "/api/v1/user/logout",
                                "/api/v1/user/register-admin").permitAll()

                        // User endpoints: accessible to USER, MODERATOR, MANAGER, ADMIN
                        .requestMatchers("/api/v1/user", "/api/v1/user/**")
                        .hasAnyRole("USER", "MODERATOR", "MANAGER", "ADMIN")

                        // Moderator endpoints
                        .requestMatchers("/api/v1/moderator/**")
                        .hasAnyRole("MODERATOR", "ADMIN")

                        // Manager endpoints
                        .requestMatchers("/api/v1/manager/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                        // Admin-only endpoints
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Handle unauthorized requests with HTTP 401 instead of redirect
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // Register custom JWT filter before username/password authentication
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Provides a password encoder bean using BCrypt hashing algorithm.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
