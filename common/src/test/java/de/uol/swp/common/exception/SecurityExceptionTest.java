package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityExceptionTest {

    @Test
    void createSecurityException() {
        SecurityException exception = new SecurityException("Test");

        assertEquals("Test", exception.getMessage());
    }

}
