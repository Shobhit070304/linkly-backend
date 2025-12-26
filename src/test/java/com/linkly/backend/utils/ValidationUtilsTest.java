package com.linkly.backend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationUtilsTest {

    @Test
    void testValidUrl() {
        assertTrue(ValidationUtils.isValidUrl("http://google.com"));
        assertTrue(ValidationUtils.isValidUrl("https://example.com"));
        assertFalse(ValidationUtils.isValidUrl("google.com"));
        assertFalse(ValidationUtils.isValidUrl(""));
        assertFalse(ValidationUtils.isValidUrl(null));
    }

    @Test
    void testValidCustomShort() {
        assertTrue(ValidationUtils.isValidCustomShort("my-link"));
        assertTrue(ValidationUtils.isValidCustomShort("abc123"));
        assertTrue(ValidationUtils.isValidCustomShort("")); // Optional
        assertFalse(ValidationUtils.isValidCustomShort("ab")); // Too short
        assertFalse(ValidationUtils.isValidCustomShort("my link")); // Space
        assertFalse(ValidationUtils.isValidCustomShort("my_link")); // Underscore
    }

    @Test
    void testReservedKeywords() {
        assertTrue(ValidationUtils.isReservedKeyword("api"));
        assertTrue(ValidationUtils.isReservedKeyword("admin"));
        assertTrue(ValidationUtils.isReservedKeyword("API"));
        assertFalse(ValidationUtils.isReservedKeyword("mylink"));
    }

}
