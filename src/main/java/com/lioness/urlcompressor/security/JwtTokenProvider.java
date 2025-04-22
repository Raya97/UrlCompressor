package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JwtTokenProvider handles creation, validation, and parsing of JWT tokens.
 * It supports both access tokens and refresh tokens with configurable lifetimes.
 */
@Service
@Slf4j
public class JwtTokenProvider {

    // Base64-encoded secret used for signing the JWT
    @Value("${jwt.secret}")
    private String base64Secret;

    // Access token validity duration: 15 minutes
    private static final long EXPIRATION_TIME = 15 * 60 * 1000;

    // Refresh token validity duration: 7 days
    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Converts the Base64 secret into a cryptographic key for signing/verifying JWTs.
     *
     * @return signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    /**
     * Generates an access token for a user with an embedded role.
     *
     * @param username the user's login/username
     * @param role     the user's role
     * @return signed JWT access token
     */
    public String generateToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role.name());

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token for a user with a longer expiration period.
     *
     * @param username the user's login/username
     * @param role     the user's role
     * @return signed JWT refresh token
     */
    public String generateRefreshToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role.name());

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(REFRESH_EXPIRATION);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the token's structure, signature, and expiration.
     *
     * @param token the JWT string
     * @return true if the token is valid; false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the JWT.
     *
     * @param token the JWT string
     * @return username
     */
    public String extractUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the user role embedded in the JWT.
     *
     * @param token the JWT string
     * @return role as an enum value
     */
    public Role extractRoleFromToken(String token) {
        String role = (String) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        return Role.valueOf(role);
    }

    /**
     * Calculates the remaining time before token expiration.
     *
     * @param token the JWT string
     * @return duration until expiration, or Duration.ZERO if invalid
     */
    public Duration getTokenTTL(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return Duration.between(
                    LocalDateTime.now(),
                    expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
        } catch (Exception e) {
            log.error("Error in getTokenTTL: {}", e.getMessage());
            return Duration.ZERO;
        }
    }

    /**
     * Extracts the raw JWT token from the "Authorization" header.
     * Expected format: "Bearer <token>"
     *
     * @param header the HTTP Authorization header
     * @return token if present and well-formed, null otherwise
     */
    public String extractTokenFromHeader(String header) {
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}
