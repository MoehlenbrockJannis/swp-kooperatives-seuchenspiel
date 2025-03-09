package de.uol.swp.common.message.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the exception message
 *
 * @see ExceptionResponseMessage
 * @since 2023-05-14
 */
public class ExceptionResponseMessageTest {

    /**
     * Test for the creation of ExceptionMessages
     *
     * This test checks if the exception message of the ExceptionMessage gets
     * set correctly during the creation of a new message
     *
     * @since 2023-05-14
     */
    @Test
    void createRegistrationExceptionMessage() {
        ExceptionResponseMessage message = new ExceptionResponseMessage("Test");

        assertEquals("Test", message.getMessage());
    }

}
