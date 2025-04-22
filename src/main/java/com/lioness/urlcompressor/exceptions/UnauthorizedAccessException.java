package com.lioness.urlcompressor.exceptions;

/**
 * UnauthorizedAccessException is thrown when a user tries to access a resource
 * or perform an action they are not authorized for (e.g., lacking proper role or token).
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedAccessException with a specific error message.
     *
     * @param message the detail message explaining the unauthorized access attempt
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
