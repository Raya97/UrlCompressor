package com.lioness.urlcompressor.notes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * NoteResponse is a generic wrapper for API responses related to notes.
 * It provides a consistent structure for success and error responses,
 * including status code, message, and optional data.
 *
 * @param <T> the type of the payload (data)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON output
public class NoteResponse<T> {

    // Indicates whether the request was successful
    private boolean success;

    // Human-readable status message (e.g. "Note created", "Invalid input")
    private String statusMessage;

    // HTTP status code (e.g. 200, 400, 404)
    private int status;

    // The actual response data (could be a Note, list of Notes, etc.)
    private T data;

    /**
     * Builds a successful NoteResponse with a payload.
     *
     * @param data    the payload to include
     * @param message a descriptive success message
     * @param status  the HTTP status (usually 200 OK)
     * @param <T>     the type of data
     * @return a fully built success response
     */
    public static <T> NoteResponse<T> success(T data, String message, HttpStatus status) {
        return new NoteResponse<>(true, message, status.value(), data);
    }

    /**
     * Builds a failed NoteResponse without any payload.
     *
     * @param message a descriptive error message
     * @param status  the HTTP error status (e.g. 400, 404, 500)
     * @param <T>     the type of data (will be null)
     * @return a failure response with no data
     */
    public static <T> NoteResponse<T> failed(String message, HttpStatus status) {
        return new NoteResponse<>(false, message, status.value(), null);
    }
}
