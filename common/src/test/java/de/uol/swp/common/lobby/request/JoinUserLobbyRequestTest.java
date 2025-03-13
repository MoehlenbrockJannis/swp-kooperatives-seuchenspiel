package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LobbyJoinUserRequest Test")
class JoinUserLobbyRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user);

    @Test
    @DisplayName("Create LobbyJoinUserRequest")
    void createLobbyJoinUserRequest() {
        JoinUserLobbyRequest request = new JoinUserLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }
}