package com.lioness.urlcompressor.exceptions;

import java.time.LocalDateTime;

/**
 * ApiErrorResponse is a DTO used to structure error responses returned by the API.
 * It contains the HTTP status code, an error message, and a timestamp.
 */
public class ApiErrorResponse {

    // The HTTP status code (e.g., 404, 500)
    private final int status;

    // A human-readable message describing the error
    private final String message;

    // The date and time when the error occurred
    private final LocalDateTime timestamp;

    /**
     * Constructs a new ApiErrorResponse.
     *
     * @param message   the error message
     * @param timestamp the time the error occurred
     * @param status    the HTTP status code
     */
    public ApiErrorResponse(String message, LocalDateTime timestamp, int status) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
