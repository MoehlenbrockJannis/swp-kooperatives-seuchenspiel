package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LobbyCreatedMessage Test")
class CreateUserLobbyServerMessageTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby" , user);

    @Test
    @DisplayName("Create LobbyCreatedServerMessage")
    void createLobbyCreatedMessage() {
        CreateUserLobbyServerMessage message = new CreateUserLobbyServerMessage(lobby, user);

        assertEquals(lobby, message.getLobby());
        assertEquals(user, message.getUser());
    }
}