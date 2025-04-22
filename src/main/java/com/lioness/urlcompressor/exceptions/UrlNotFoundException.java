package com.lioness.urlcompressor.exceptions;

/**
 * UrlNotFoundException is thrown when a full (original) URL
 * or a shortened version cannot be found in the database.
 */
public class UrlNotFoundException extends RuntimeException {

    /**
     * Constructs a new UrlNotFoundException with a specific error message.
     *
     * @param message the detail message explaining why the URL was not found
     */
    public UrlNotFoundException(String message) {
        super(message);
    }
}
