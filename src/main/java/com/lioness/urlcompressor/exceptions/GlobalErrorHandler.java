package com.lioness.urlcompressor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * GlobalErrorHandler is a centralized exception handler for REST APIs.
 * It catches specific and general exceptions and returns structured error responses.
 */
@RestControllerAdvice
public class GlobalErrorHandler {

    /**
     * Handles the case when a shortened URL is not found.
     *
     * @param ex the exception thrown when the short URL doesn't exist
     * @return a structured error response with HTTP 404 Not Found
     */
    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleShortUrlNotFound(ShortUrlNotFoundException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other unexpected exceptions that are not explicitly caught elsewhere.
     *
     * @param ex the exception that was thrown
     * @return a structured error response with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedError(Exception ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "Internal server error. Please try again later.",
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
