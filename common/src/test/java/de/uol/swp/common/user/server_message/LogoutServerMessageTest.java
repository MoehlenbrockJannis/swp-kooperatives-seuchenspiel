package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogoutServerMessageTest {

    @Test
    void createUserLoggedOutMessage() {
        final User user = new UserDTO("Test", "", "");

        LogoutServerMessage message = new LogoutServerMessage(user);

        assertEquals(user, message.getUser());
    }

}
