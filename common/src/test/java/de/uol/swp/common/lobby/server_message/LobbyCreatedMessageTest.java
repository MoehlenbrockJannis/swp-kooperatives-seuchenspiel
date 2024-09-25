package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the lobby created message
 *
 * @see CreateLobbyServerMessage
 * @since 2023-05-14
 */
@DisplayName("LobbyCreatedMessage Test")
class LobbyCreatedMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby" , user, 2, 4);

    /**
     * Test for creation of the LobbyCreatedMessages
     *
     * This test checks if the lobbyName and the user of the LobbyCreatedServerMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    @DisplayName("Create LobbyCreatedServerMessage")
    void createLobbyCreatedMessage() {
        CreateLobbyServerMessage message = new CreateLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }
}