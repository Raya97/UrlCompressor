package com.lioness.urlcompressor.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongUrlValidatorTest {

    private final LongUrlValidator validator = new LongUrlValidator();

    /**
     * 🚫 Null input should not pass validation.
     */
    @Test
    void isValid_shouldReturnFalse_whenUrlIsNull() {
        assertFalse(validator.isValid(null), "null must be invalid");
    }

    /**
     * 🚫 Empty string is not a valid URL.
     */
    @Test
    void isValid_shouldReturnFalse_whenUrlIsEmpty() {
        assertFalse(validator.isValid(""), "Empty string must be invalid");
    }

    /**
     * ✅ Standard http:// URL should be valid.
     */
    @Test
    void isValid_shouldReturnTrue_whenUrlStartsWithHttp() {
        assertTrue(validator.isValid("http://example.com"), "Valid http:// URL");
    }

    /**
     * ✅ Standard https:// URL should be valid.
     */
    @Test
    void isValid_shouldReturnTrue_whenUrlStartsWithHttps() {
        assertTrue(validator.isValid("https://example.com"), "Valid https:// URL");
    }

    /**
     * ❌ Non-http protocols should fail the current validator logic.
     */
    @Test
    void isValid_shouldReturnFalse_whenUrlDoesNotStartWithHttpOrHttps() {
        assertFalse(validator.isValid("ftp://example.com"), "ftp should be invalid");
        assertFalse(validator.isValid("www.example.com"), "no protocol should be invalid");
    }

    /**
     * ⚠️ Case sensitivity check — current logic requires lowercase "http"/"https".
     */
    @Test
    void isValid_shouldBeCaseSensitive() {
        assertFalse(validator.isValid("HTTPS://example.com"), "Uppercase HTTPS is invalid in current logic");
    }
}
