package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginServerMessageTest {

    @Test
    void createUserLoggedInMessage() {
        final User user = new UserDTO("Test", "", "");

        LoginServerMessage message = new LoginServerMessage(user);

        assertEquals(user, message.getUser());
    }

}
