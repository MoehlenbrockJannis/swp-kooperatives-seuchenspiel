package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetrieveAllOnlineUsersServerMessageTest {

    final ArrayList<User> users = new ArrayList<>();

    @Test
    void createUserLoggedOutMessage() {
        RetrieveAllOnlineUsersServerMessage message = new RetrieveAllOnlineUsersServerMessage(users);

        assertEquals(users, message.getUsers());
    }

}
