package com.lioness.urlcompressor.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalValidationHandler handles validation errors across the entire application.
 * It catches MethodArgumentNotValidException and returns a clean JSON map of field errors.
 */
@RestControllerAdvice
public class GlobalValidationHandler {

    /**
     * Handles validation errors triggered by @Valid annotations on request DTOs.
     * Extracts all field-specific error messages and returns them in a structured map.
     *
     * Example response:
     * {
     *   "username": "must not be blank",
     *   "password": "size must be between 6 and 20"
     * }
     *
     * @param ex the exception thrown when validation fails
     * @return HTTP 400 Bad Request with a map of field-to-message entries
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Iterate through field errors and extract field names and corresponding messages
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Return a 400 Bad Request with the structured error map
        return ResponseEntity.badRequest().body(errors);
    }
}
