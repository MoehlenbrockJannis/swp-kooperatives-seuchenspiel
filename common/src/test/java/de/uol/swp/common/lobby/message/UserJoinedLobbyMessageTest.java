package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the user joined lobby message
 *
 * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
 * @since 2023-05-14
 */
class UserJoinedLobbyMessageTest {

    final String lobbyName = "Test";
    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");


    /**
     * Test for creation of the UserJoinedLobbyMessages
     *
     * This test checks if the lobbyName and the user of the UserJoinedLobbyMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createUserJoinedLobbyMessage() {
        UserJoinedLobbyMessage message = new UserJoinedLobbyMessage(lobbyName, user);

        assertEquals(lobbyName, message.getLobbyName());
        assertEquals(user, message.getUser());
    }

}
