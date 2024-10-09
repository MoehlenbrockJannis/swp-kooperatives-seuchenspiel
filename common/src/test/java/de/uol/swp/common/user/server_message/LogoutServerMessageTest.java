package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the user logged out message
 *
 * @see LogoutServerMessage
 * @since 2023-05-14
 */
class LogoutServerMessageTest {

    /**
     * Test for the creation of UserLoggedOutMessages
     *
     * This test checks if the username of the UserLoggedOutMessage gets
     * set correctly during the creation of a new message
     *
     * @since 2023-05-14
     */
    @Test
    void createUserLoggedOutMessage() {
        final User user = new UserDTO("Test", "", "");

        LogoutServerMessage message = new LogoutServerMessage(user);

        assertEquals(user, message.getUser());
    }

}
