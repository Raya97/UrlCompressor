package com.lioness.urlcompressor.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * RegistrationResponse is returned after a user completes the registration process.
 * It contains user details (such as ID and login) and a status message.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from the JSON output
public class RegistrationResponse extends BaseUserResponse {

    /**
     * The ID of the newly registered user.
     */
    private Long userId;

    /**
     * The login (username) of the new user.
     */
    private String login;

    /**
     * Constructor with message and HTTP status (typically used for failure).
     */
    public RegistrationResponse(String message, HttpStatus status) {
        super(message, status);
    }

    /**
     * Constructor with full response data for a successful registration.
     */
    public RegistrationResponse(Long userId, String login, String message, HttpStatus status) {
        super(message, status);
        this.userId = userId;
        this.login = login;
    }

    /**
     * Factory method to create a successful registration response.
     *
     * @param userId the new user's ID
     * @param login  the new user's login name
     * @return a populated RegistrationResponse
     */
    public static RegistrationResponse success(Long userId, String login) {
        return new RegistrationResponse(userId, login, "Registration successful", HttpStatus.OK);
    }

    /**
     * Factory method to create a failed registration response.
     *
     * @param message failure reason
     * @param status  HTTP error status
     * @return an error RegistrationResponse
     */
    public static RegistrationResponse failed(String message, HttpStatus status) {
        return new RegistrationResponse(null, null, message, status);
    }
}
