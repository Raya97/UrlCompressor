package com.lioness.urlcompressor.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for MessageUtility — static message generation and constants.
 */
class MessageUtilityTest {

    @Test
    @DisplayName("userAlreadyExists() returns correct message")
    void testUserAlreadyExistsMessage() {
        String username = "lioness";
        String expected = "Користувач з ім'ям lioness вже існує. Виберіть інше ім'я.";
        assertEquals(expected, MessageUtility.userAlreadyExists(username));
    }

    @Test
    @DisplayName("userNotFound() returns correct message")
    void testUserNotFoundMessage() {
        String username = "ghost";
        String expected = "Користувач з ім'ям ghost не знайдений.";
        assertEquals(expected, MessageUtility.userNotFound(username));
    }

    @Test
    @DisplayName("All static fields return expected messages")
    void testStaticMessages() {
        // 📌 Validate all important predefined messages
        assertEquals("Невірний токен.", MessageUtility.INCORRECT_TOKEN_MESSAGE);
        assertEquals("Ім'я користувача повинно містити від 5 до 50 символів.", MessageUtility.USERNAME_REQUIREMENTS);
        assertEquals("Пароль має містити щонайменше 8 символів, а також цифри, великі та малі літери.", MessageUtility.PASSWORD_REQUIREMENTS);
        assertEquals("Невірний пароль. Спробуйте ще раз.", MessageUtility.INVALID_PASSWORD);
        assertEquals("URL-адресу успішно створено.", MessageUtility.URL_CREATED);
        assertEquals("URL-адресу успішно оновлено.", MessageUtility.URL_UPDATED);
        assertEquals("URL-адресу успішно видалено.", MessageUtility.URL_DELETED);
    }
}
