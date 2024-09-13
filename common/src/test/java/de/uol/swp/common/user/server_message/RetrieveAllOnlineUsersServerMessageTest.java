package de.uol.swp.common.user.server_message;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the users list message
 *
 * @see RetrieveAllOnlineUsersServerMessage
 * @since 2023-05-14
 */
class RetrieveAllOnlineUsersServerMessageTest {

    final ArrayList<User> users = new ArrayList<>();

    /**
     * Test for the creation of UsersListMessages
     *
     * This test checks if the user list of the UsersListMessage gets
     * set correctly during the creation of a new message
     *
     * @since 2023-05-14
     */
    @Test
    void createUserLoggedOutMessage() {
        RetrieveAllOnlineUsersServerMessage message = new RetrieveAllOnlineUsersServerMessage(users);

        assertEquals(users, message.getUsers());
    }

}
