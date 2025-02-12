package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JoinUserUserAlreadyInLobbyLobbyResponse Test")
class JoinUserUserAlreadyInLobbyLobbyResponseTest {

    private Lobby testLobby;
    private User owner;
    private User existingUser;

    @BeforeEach
    void setUp() {
        owner = new UserDTO("owner", "password", "owner@example.com");
        existingUser = new UserDTO("existing", "password", "existing@example.com");
        testLobby = new LobbyDTO("TestLobby", owner);
        testLobby.setStatus(LobbyStatus.OPEN);
        testLobby.joinUser(existingUser);
    }

    @Test
    @DisplayName("Constructor should handle null lobby and null user")
    void nullLobbyAndUserTest() {
        JoinUserUserAlreadyInLobbyLobbyResponse response = new JoinUserUserAlreadyInLobbyLobbyResponse(null, null);

        assertNull(response.getLobby());
        assertNull(response.getUser());
    }

    @Test
    @DisplayName("Constructor should handle null lobby")
    void nullLobbyTest() {
        JoinUserUserAlreadyInLobbyLobbyResponse response = new JoinUserUserAlreadyInLobbyLobbyResponse(null, existingUser);

        assertNull(response.getLobby());
        assertNotNull(response.getUser());
        assertEquals(existingUser, response.getUser());
    }

    @Test
    @DisplayName("Constructor should handle null user")
    void nullUserTest() {
        JoinUserUserAlreadyInLobbyLobbyResponse response = new JoinUserUserAlreadyInLobbyLobbyResponse(testLobby, null);

        assertNotNull(response.getLobby());
        assertNull(response.getUser());
        assertEquals(testLobby, response.getLobby());
    }

    @Test
    @DisplayName("Response should handle user already in lobby")
    void userAlreadyInLobbyTest() {
        JoinUserUserAlreadyInLobbyLobbyResponse response = new JoinUserUserAlreadyInLobbyLobbyResponse(testLobby, existingUser);

        assertNotNull(response.getLobby());
        assertNotNull(response.getUser());
        assertEquals(testLobby, response.getLobby());
        assertEquals(existingUser, response.getUser());
        assertTrue(response.getLobby().getUsers().contains(response.getUser()));
    }
}