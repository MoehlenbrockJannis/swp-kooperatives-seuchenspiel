package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the user joined lobby message
 *
 * @see JoinUserLobbyServerMessage
 * @since 2023-05-14
 */
@DisplayName("UserJoinedLobbyMessage Test")
class UserJoinedLobbyMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("LobbyTest", user);

    /**
     * Test for creation of the UserJoinedLobbyMessages
     *
     * This test checks if the lobbyName and the user of the UserJoinedLobbyServerMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    @DisplayName("Create UserJoinedLobbyServerMessage")
    void createUserJoinedLobbyMessage() {
        JoinUserLobbyServerMessage message = new JoinUserLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }
}