package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.user.dto.BaseUserResponse;
import com.lioness.urlcompressor.util.MessageUtility;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserInputValidator is responsible for validating user input during registration or login.
 * It checks that the username and password meet minimum format and security requirements.
 */
@Service
public class UserInputValidator {

    private static final int USERNAME_MIN_LENGTH = 5;
    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * Validates the given username and password according to business rules.
     *
     * @param login    the username to validate
     * @param password the password to validate
     * @return Optional containing an error response if validation fails, otherwise empty
     */
    public Optional<BaseUserResponse> validate(String login, String password) {
        if (!isUsernameValid(login)) {
            return Optional.of(BaseUserResponse.builder()
                    .message(MessageUtility.USERNAME_REQUIREMENTS)
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        if (!isPasswordValid(password)) {
            return Optional.of(BaseUserResponse.builder()
                    .message(MessageUtility.PASSWORD_REQUIREMENTS)
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        return Optional.empty();
    }

    /**
     * Checks if the username is valid in terms of length.
     * Username must be between {@value USERNAME_MIN_LENGTH} and {@value USERNAME_MAX_LENGTH} characters.
     *
     * @param login the username to validate
     * @return true if valid, false otherwise
     */
    private boolean isUsernameValid(String login) {
        return login != null && login.length() >= USERNAME_MIN_LENGTH && login.length() <= USERNAME_MAX_LENGTH;
    }

    /**
     * Checks if the password is secure.
     * The password must contain:
     * - at least one lowercase letter,
     * - at least one uppercase letter,
     * - at least one digit,
     * - and be at least {@value PASSWORD_MIN_LENGTH} characters long.
     *
     * @param password the password to validate
     * @return true if the password meets all criteria
     */
    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH) {
            return false;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }
}
