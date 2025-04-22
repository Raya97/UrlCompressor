package com.lioness.urlcompressor.util;

import org.springframework.stereotype.Service;

/**
 * MessageUtility centralizes all user-facing messages for validation,
 * authentication, URL processing, and general system operations.
 * Useful for localization and maintenance of consistent text responses.
 */
@Service
public class MessageUtility {

    public static final String INCORRECT_TOKEN_MESSAGE = "Invalid token.";

    /**
     * Username format requirements.
     */
    public static final String USERNAME_REQUIREMENTS =
            "Username must be between 5 and 50 characters.";

    /**
     * Password strength requirements.
     */
    public static final String PASSWORD_REQUIREMENTS =
            "Password must be at least 8 characters long and include digits, uppercase, and lowercase letters.";

    /**
     * Message for wrong password.
     */
    public static final String INVALID_PASSWORD = "Incorrect password. Please try again.";

    public static final String INVALID_URL = "The provided URL is not valid.";
    public static final String UNAUTHORIZED_ACCESS = "Please log in to access your URLs.";
    public static final String EMPTY_URL_LIST = "Your list of URLs is empty.";
    public static final String URL_NOT_FOUND = "The requested URL was not found.";
    public static final String INVALID_TOKEN = "Invalid token. Please try again.";
    public static final String EXPIRED_URL = "The URL has expired.";
    public static final String INVALID_EXPIRATION_DATE = "Expiration date cannot be in the past.";
    public static final String URL_CREATED = "URL has been successfully created.";
    public static final String URL_UPDATED = "URL has been successfully updated.";
    public static final String URL_DELETED = "URL has been successfully deleted.";

    /**
     * Generates a message when a user already exists.
     *
     * @param username the conflicting username
     * @return message indicating the user already exists
     */
    public static String userAlreadyExists(String username) {
        return "A user with the name " + username + " already exists. Please choose a different name.";
    }

    /**
     * Generates a message when a user is not found.
     *
     * @param username the missing username
     * @return message indicating the user was not found
     */
    public static String userNotFound(String username) {
        return "No user found with the name " + username + ".";
    }
}
