package com.lioness.urlcompressor.user;

/**
 * Role defines the available user roles within the application.
 * These roles are used to control access to protected resources and endpoints.
 */
public enum Role {

    /**
     * Standard user with access to basic functionality.
     */
    USER,

    /**
     * Administrator with full access to all system operations and endpoints.
     */
    ADMIN,

    /**
     * Moderator with permissions to manage content or users, but limited admin capabilities.
     */
    MODERATOR
}
