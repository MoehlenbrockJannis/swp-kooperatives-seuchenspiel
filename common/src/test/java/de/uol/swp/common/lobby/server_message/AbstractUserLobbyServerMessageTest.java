package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AbstractLobbyServerMessage Test")
class AbstractUserLobbyServerMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final UserDTO user1 = new UserDTO("Marco1", "Marco1", "Marco1@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user);

    @Test
    @DisplayName("Create AbstractLobbyServerMessage")
    void createAbstractLobbyMessage() {
        AbstractUserLobbyServerMessage message = new AbstractUserLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }

    @Test
    @DisplayName("Set new lobby and user in AbstractLobbyServerMessage")
    void setAbstractLobbyNameAndUser() {
        AbstractUserLobbyServerMessage message = new AbstractUserLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());

        message.setLobby(lobby);
        message.setUser(user1);

        assertEquals(lobby, message.getLobby());
        assertEquals(user1, message.getUser());
    }
}