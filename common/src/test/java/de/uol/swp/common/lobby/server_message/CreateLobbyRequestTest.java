package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the create lobby request
 *
 * @see CreateLobbyRequest
 * @since 2023-05-14
 */
class CreateLobbyRequestTest {

    final UserDTO user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    final UserDTO user1 = new UserDTO("Marco1", "Marco1", "Marco1@Grawunder.com");
    final Lobby lobby = new LobbyDTO("TestLobby", user);


    /**
     * Test for creation of the CreateLobbyRequests
     *
     * This test checks if the lobbyName and the user of the CreateLobbyRequest gets
     * set correctly during the creation of the request
     *
     * @since 2023-05-14
     */
    @Test
    void createCreateLobbyRequest() {
        CreateLobbyRequest request = new CreateLobbyRequest(lobby, user);

        assertEquals(lobby, request.getLobby());
        assertEquals(user, request.getUser());
    }

    /**
     * Test for set new owner of the AbstractLobbyRequests
     *
     * This test checks if the owner of the AbstractLobbyRequest gets
     * set correctly during setting new owner of the request
     *
     * @since 2023-05-14
     */
    @Test
    void setCreateLobbyRequestNameAndUser() {
        CreateLobbyRequest request = new CreateLobbyRequest(lobby, user);

        assertEquals(user, request.getUser());

        request.setUser(user1);

        assertEquals(user1, request.getUser());
    }

}
