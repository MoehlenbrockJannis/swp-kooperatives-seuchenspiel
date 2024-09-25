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

@DisplayName("LobbyJoinUserResponse Test")
class LobbyJoinUserResponseTest {

    private Lobby testLobby;
    private User owner;
    private User joiningUser;

    @BeforeEach
    void setUp() {
        owner = new UserDTO("owner", "password", "owner@example.com");
        joiningUser = new UserDTO("joiner", "password", "joiner@example.com");
        testLobby = new LobbyDTO("TestLobby", owner, 2, 4);
        testLobby.setStatus(LobbyStatus.OPEN);
    }

    @Test
    @DisplayName("Constructor should handle null lobby and null user")
    void nullLobbyAndUserTest() {
        LobbyJoinUserResponse response = new LobbyJoinUserResponse(null, null);

        assertNull(response.getLobby());
        assertNull(response.getUser());
    }

    @Test
    @DisplayName("Constructor should handle null lobby")
    void nullLobbyTest() {
        LobbyJoinUserResponse response = new LobbyJoinUserResponse(null, joiningUser);

        assertNull(response.getLobby());
        assertNotNull(response.getUser());
        assertEquals(joiningUser, response.getUser());
    }

    @Test
    @DisplayName("Constructor should handle null user")
    void nullUserTest() {
        LobbyJoinUserResponse response = new LobbyJoinUserResponse(testLobby, null);

        assertNotNull(response.getLobby());
        assertNull(response.getUser());
        assertEquals(testLobby, response.getLobby());
    }
}