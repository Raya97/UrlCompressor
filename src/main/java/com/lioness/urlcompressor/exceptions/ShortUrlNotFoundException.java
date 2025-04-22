package com.lioness.urlcompressor.exceptions;

/**
 * ShortUrlNotFoundException is thrown when a requested short URL
 * does not exist in the system or cannot be found in the database.
 */
public class ShortUrlNotFoundException extends RuntimeException {

    /**
     * Constructs a new ShortUrlNotFoundException with a specific error message.
     *
     * @param message the detail message explaining why the exception occurred
     */
    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}
