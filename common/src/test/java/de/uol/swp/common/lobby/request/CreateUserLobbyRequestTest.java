package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CreateLobbyRequest Test")
class CreateUserLobbyRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final UserDTO user1 = new UserDTO("Marco1", "Marco1", "Marco1@Grawunder.com");
    final Lobby lobby = new LobbyDTO("TestLobby", user);

    @Test
    @DisplayName("Create CreateLobbyRequest")
    void createCreateLobbyRequest() {
        CreateUserLobbyRequest request = new CreateUserLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }

    @Test
    @DisplayName("Set new user in CreateLobbyRequest")
    void setCreateLobbyRequestNameAndUser() {
        CreateUserLobbyRequest request = new CreateUserLobbyRequest(lobby, user);

        assertEquals(user, request.getUser());

        request.setUser(user1);

        assertEquals(user1, request.getUser());
    }
}