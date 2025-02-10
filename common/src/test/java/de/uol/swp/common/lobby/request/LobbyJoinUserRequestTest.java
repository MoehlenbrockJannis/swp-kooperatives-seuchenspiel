package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the lobby join user request
 *
 * @see JoinUserLobbyRequest
 * @since 2023-05-14
 */
@DisplayName("LobbyJoinUserRequest Test")
class LobbyJoinUserRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user);

    /**
     * Test for creation of the LobbyJoinUserRequests
     *
     * This test checks if the lobbyName and the user of the LobbyJoinUserRequest gets
     * set correctly during the creation of the request
     *
     * @since 2023-05-14
     */
    @Test
    @DisplayName("Create LobbyJoinUserRequest")
    void createLobbyJoinUserRequest() {
        JoinUserLobbyRequest request = new JoinUserLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }
}