package com.ecoaccess.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.ecoaccess.util.Validation;
import com.ecoaccess.exception.AppExceptions.ValidationException;

class ValidationTest {
    @Test
    void acceptsValidRegistrationFields() {
        assertEquals("9876543210", Validation.mobile("+919876543210"));
        assertEquals("Ashish Sharma", Validation.name("Ashish Sharma"));
        assertEquals("Test@123", Validation.password("Test@123"));
    }

    @Test
    void rejectsBadRegistrationFields() {
        assertThrows(ValidationException.class, () -> Validation.mobile("123"));
        assertThrows(ValidationException.class, () -> Validation.email("not-email"));
        assertThrows(ValidationException.class, () -> Validation.password("weak"));
    }

    @Test
    void rejectsInvalidCard() {
        assertThrows(ValidationException.class, () -> Validation.card("A", "123", "13/26", "1"));
    }
}
