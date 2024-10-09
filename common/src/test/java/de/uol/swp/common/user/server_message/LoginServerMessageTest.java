package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the user logged in message
 *
 * @see LoginServerMessage
 * @since 2023-05-14
 */
class LoginServerMessageTest {

    /**
     * Test for the creation of UserLoggedInMessages
     *
     * This test checks if the username of the UserLoggedInMessage gets
     * set correctly during the creation of a new message
     *
     * @since 2023-05-14
     */
    @Test
    void createUserLoggedInMessage() {
        final User user = new UserDTO("Test", "", "");

        LoginServerMessage message = new LoginServerMessage(user);

        assertEquals(user, message.getUser());
    }

}
