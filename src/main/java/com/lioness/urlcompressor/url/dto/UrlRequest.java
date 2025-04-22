package com.lioness.urlcompressor.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UrlRequest is a Data Transfer Object used for handling requests related to URL operations.
 * It contains the original (long) URL to be shortened, an optional expiration time,
 * and an Authorization header for user validation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UrlRequest {

    /**
     * The original long URL that should be shortened.
     * Required for creating or resolving short links.
     */
    private String originalUrl;

    /**
     * Optional expiration time — the short URL becomes invalid after this timestamp.
     * If null, the URL never expires.
     */
    private LocalDateTime expirationTime;

    /**
     * The Authorization header passed in the request (usually "Bearer <token>").
     * Used to authenticate the user.
     */
    private String authHeader;

    /**
     * Custom setter for setting the Authorization header manually.
     * Useful when extracting the header inside a controller.
     *
     * @param authHeader the Authorization value to assign
     */
    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
