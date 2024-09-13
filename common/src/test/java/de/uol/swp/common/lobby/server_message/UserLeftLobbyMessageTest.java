package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the user left lobby message
 *
 * @see LobbyLeaveUserServerMessage
 * @since 2023-05-14
 */
class UserLeftLobbyMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user);


    /**
     * Test for creation of the UserLeftLobbyMessages
     *
     * This test checks if the lobbyName and the user of the UserLeftLobbyServerMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createUserLeftLobbyMessage() {
        LobbyLeaveUserServerMessage message = new LobbyLeaveUserServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }

}
