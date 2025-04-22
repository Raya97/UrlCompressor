package com.lioness.urlcompressor.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * AuthenticationResponse is a DTO returned after a user logs in or registers.
 * It includes access and refresh tokens along with response metadata.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude nulls in JSON output
public class AuthenticationResponse extends BaseUserResponse {

    /**
     * JWT access token for authenticated access to protected routes.
     */
    private String accessToken;

    /**
     * Refresh token used to obtain a new access token when it expires.
     */
    private String refreshToken;

    /**
     * Constructor with message, access token, and refresh token.
     * Used when authentication is successful.
     */
    public AuthenticationResponse(String message, String accessToken, String refreshToken) {
        super(message);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Constructor with message and HTTP status.
     * Used for failed authentication responses.
     */
    public AuthenticationResponse(String message, HttpStatus status) {
        super(message, status);
    }

    /**
     * Constructor with access token, message, and HTTP status.
     * Used when refresh token is not returned or not needed.
     */
    public AuthenticationResponse(String accessToken, String message, HttpStatus status) {
        super(message, status);
        this.accessToken = accessToken;
    }

    /**
     * Factory method for successful authentication with tokens.
     */
    public static AuthenticationResponse success(String accessToken, String refreshToken) {
        return new AuthenticationResponse("Authentication successful", accessToken, refreshToken);
    }

    /**
     * Factory method for failed authentication.
     */
    public static AuthenticationResponse failed(String message) {
        return new AuthenticationResponse("Authentication failed: " + message, HttpStatus.UNAUTHORIZED);
    }
}
