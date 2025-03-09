package de.uol.swp.common.user.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the registration exception message
 *
 * @see RegisterUserExceptionResponse
 * @since 2023-05-14
 */
public class RegisterUserExceptionResponseTest {

    /**
     * Test for the creation of RegistrationExceptionMessages
     *
     * This test checks if the exception message of the RegistrationExceptionMessage gets
     * set correctly during the creation of a new message
     *
     * @since 2023-05-14
     */
    @Test
    void createRegistrationExceptionMessage() {
        RegisterUserExceptionResponse message = new RegisterUserExceptionResponse("Test");

        assertEquals("RegisterUserExceptionResponse: Test", message.toString());
    }

}
