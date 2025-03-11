package de.uol.swp.common.user.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserExceptionResponseTest {

    @Test
    void createRegistrationExceptionMessage() {
        RegisterUserExceptionResponse message = new RegisterUserExceptionResponse("Test");

        assertEquals("RegisterUserExceptionResponse: Test", message.toString());
    }

}
