package de.uol.swp.common.message.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionResponseMessageTest {

    @Test
    void createRegistrationExceptionMessage() {
        ExceptionResponseMessage message = new ExceptionResponseMessage("Test");

        assertEquals("Test", message.getMessage());
    }

}
