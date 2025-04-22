package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * AuthorizationService provides logic to authenticate users based on JWT tokens.
 * It is used to extract and validate tokens from the Authorization header,
 * and fetch the corresponding UserEntity from the database.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    // Repository to find users by login (case-insensitive)
    private final UserRepository userRepository;

    // Utility for extracting and validating JWT tokens
    private final JwtTokenProvider tokenProvider;

    /**
     * Retrieves the authorized user from the Authorization header if the token is valid.
     *
     * @param authorizationHeader the HTTP Authorization header (usually "Bearer <token>")
     * @return Optional containing the UserEntity if authenticated; empty otherwise
     */
    public Optional<UserEntity> getAuthorizedUser(String authorizationHeader) {
        // Extract token from "Bearer ..." header
        String token = tokenProvider.extractTokenFromHeader(authorizationHeader);

        // Validate token and retrieve user if valid
        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.extractUsernameFromToken(token);
            return userRepository.findByLoginIgnoreCase(username);
        }

        // Return empty if token is missing or invalid
        return Optional.empty();
    }
}
