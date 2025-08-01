package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AbstractLobbyRequest Test")
class AbstractUserLobbyRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final UserDTO user1 = new UserDTO("Marco1", "Marco1", "Marco1@Grawunder.com");
    final Lobby lobby = new LobbyDTO("TestLobby", user);

    @Test
    @DisplayName("Create AbstractLobbyRequest")
    void createAbstractLobbyRequest() {
        AbstractUserLobbyRequest request = new AbstractUserLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }

    @Test
    @DisplayName("Set new lobby and user in AbstractLobbyRequest")
    void setAbstractLobbyNameAndUser() {
        AbstractUserLobbyRequest request = new AbstractUserLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());

        request.setLobby(lobby);
        request.setUser(user1);

        assertEquals(lobby, request.getLobby());
        assertEquals(user1, request.getUser());
    }
}