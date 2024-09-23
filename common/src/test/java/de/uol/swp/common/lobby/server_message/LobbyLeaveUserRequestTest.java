package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the lobby leave user request
 *
 * @see LobbyLeaveUserRequest
 * @since 2023-05-14
 */
class LobbyLeaveUserRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final LobbyDTO lobby = new LobbyDTO("TestLobby", user, 2, 4);


    /**
     * Test for creation of the LobbyLeaveUserRequests
     *
     * This test checks if the lobbyName and the user of the LobbyLeaveUserRequest gets
     * set correctly during the creation of the request
     *
     * @since 2023-05-14
     */
    @Test
    void createLobbyLeaveUserRequest() {
        LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }

}
