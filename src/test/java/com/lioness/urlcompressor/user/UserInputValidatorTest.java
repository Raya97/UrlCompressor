package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.user.dto.BaseUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for the UserInputValidator class — verifies login and password validation logic.
 */
class UserInputValidatorTest {

    private UserInputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserInputValidator();
    }

    @Test
    void validate_shouldFail_whenLoginIsTooShort() {
        // Login has less than 5 characters — should fail
        Optional<BaseUserResponse> result = validator.validate("abc", "Password123");
        assertTrue(result.isPresent());
        assertEquals("Ім'я користувача повинно містити від 5 до 50 символів.", result.get().getMessage());
    }

    @Test
    void validate_shouldFail_whenLoginIsTooLong() {
        // Login has more than 50 characters — should fail
        String longLogin = "a".repeat(51);
        Optional<BaseUserResponse> result = validator.validate(longLogin, "Password123");
        assertTrue(result.isPresent());
        assertEquals("Ім'я користувача повинно містити від 5 до 50 символів.", result.get().getMessage());
    }

    @Test
    void validate_shouldFail_whenPasswordTooShort() {
        // Password shorter than 8 characters — should fail
        Optional<BaseUserResponse> result = validator.validate("validLogin", "abc");
        assertTrue(result.isPresent());
        assertEquals("Пароль має містити щонайменше 8 символів, а також цифри, великі та малі літери.", result.get().getMessage());
    }

    @Test
    void validate_shouldFail_whenPasswordMissingDigit() {
        // Password lacks digits — should fail
        Optional<BaseUserResponse> result = validator.validate("validLogin", "Password");
        assertTrue(result.isPresent());
        assertEquals("Пароль має містити щонайменше 8 символів, а також цифри, великі та малі літери.", result.get().getMessage());
    }

    @Test
    void validate_shouldFail_whenPasswordMissingUppercase() {
        // Password lacks uppercase letters — should fail
        Optional<BaseUserResponse> result = validator.validate("validLogin", "password1");
        assertTrue(result.isPresent());
        assertEquals("Пароль має містити щонайменше 8 символів, а також цифри, великі та малі літери.", result.get().getMessage());
    }

    @Test
    void validate_shouldFail_whenPasswordMissingLowercase() {
        // Password lacks lowercase letters — should fail
        Optional<BaseUserResponse> result = validator.validate("validLogin", "PASSWORD1");
        assertTrue(result.isPresent());
        assertEquals("Пароль має містити щонайменше 8 символів, а також цифри, великі та малі літери.", result.get().getMessage());
    }

    @Test
    void validate_shouldPass_whenAllValid() {
        // Login and password meet all criteria — should pass (empty Optional)
        Optional<BaseUserResponse> result = validator.validate("validUser", "Pass1234");
        assertTrue(result.isEmpty());
    }
}
