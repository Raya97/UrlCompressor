package com.lioness.urlcompressor.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * BaseUserResponse is the parent class for all user-related response DTOs.
 * It contains common fields like a message and an optional HTTP status.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Skip null fields in JSON response
public class BaseUserResponse {

    /**
     * Message describing the result of the request (e.g. success or error).
     */
    private String message;

    /**
     * HTTP status code of the response.
     * This is excluded from JSON serialization (used internally only).
     */
    @JsonIgnore
    private HttpStatus status;

    /**
     * Constructor with just a message.
     * Defaults to HTTP 200 OK status.
     */
    public BaseUserResponse(String message) {
        this.message = message;
        this.status = HttpStatus.OK;
    }

    /**
     * Constructor with both message and HTTP status.
     * Used when a specific status code must be returned (e.g. 401, 400).
     */
    public BaseUserResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
