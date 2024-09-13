package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the abstract lobby message
 *
 * @see AbstractLobbyServerMessage
 * @since 2023-05-14
 */
class AbstractLobbyMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final UserDTO user1 = new UserDTO("Marco1", "Marco1", "Marco1@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user);


    /**
     * Test for creation of the AbstractLobbyMessages
     *
     * This test checks if the lobbyName and the user of the AbstractLobbyServerMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createAbstractLobbyMessage() {
        AbstractLobbyServerMessage message = new AbstractLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }

    /**
     * Test for set new lobbyName and new user of the AbstractLobbyMessages
     *
     * This test checks if the lobbyName and the user of the AbstractLobbyServerMessage gets
     * set correctly during setting new lobbyName and user of the message
     *
     * @since 2023-05-14
     */
    @Test
    void setAbstractLobbyNameAndUser() {
        AbstractLobbyServerMessage message = new AbstractLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());

        message.setLobby(lobby);
        message.setUser(user1);

        assertEquals(lobby, message.getLobby());
        assertEquals(user1, message.getUser());
    }

}
