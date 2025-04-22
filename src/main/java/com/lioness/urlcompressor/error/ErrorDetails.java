package com.lioness.urlcompressor.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * ErrorDetails represents the structure of a standard error response.
 * It contains metadata such as timestamp, error message, HTTP status,
 * exception type, and the request path where the error occurred.
 */
@Getter
@Builder
@AllArgsConstructor
public class ErrorDetails {

    // The date and time when the error occurred
    private LocalDateTime timestamp;

    // A short, human-readable message describing the error
    private final String message;

    // The HTTP status associated with the error
    private final HttpStatus status;

    // The type of the exception (e.g. NullPointerException)
    private String exception;

    // The URI path where the error occurred
    private String path;
}
