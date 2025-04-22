package com.lioness.urlcompressor.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UserRequestDTO is used for user login and registration requests.
 * It contains the login (username) and password fields, with validation.
 */
@AllArgsConstructor
@Data
public class UserRequestDTO {

    /**
     * The login/username for the user.
     * Must be between 3 and 50 characters and cannot be blank.
     */
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    @NotBlank(message = "Login is required")
    private String login;

    /**
     * The password for the user account.
     * Must be between 6 and 100 characters and cannot be blank.
     */
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Trims leading/trailing whitespace from the login before validation.
     *
     * @param login the input login
     */
    public void setLogin(String login) {
        this.login = login == null ? null : login.trim();
    }

    /**
     * Trims leading/trailing whitespace from the password before validation.
     *
     * @param password the input password
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}
