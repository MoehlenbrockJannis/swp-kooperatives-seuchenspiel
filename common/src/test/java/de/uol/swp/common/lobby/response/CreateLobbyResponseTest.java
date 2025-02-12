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

@DisplayName("CreateLobbyResponse Test")
class CreateLobbyResponseTest {

    private Lobby testLobby;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new UserDTO("owner", "password", "owner@example.com");
        testLobby = new LobbyDTO("TestLobby", owner);
        testLobby.setStatus(LobbyStatus.OPEN);
    }

    @Test
    @DisplayName("Constructor should handle null lobby")
    void nullLobbyTest() {
        CreateLobbyResponse response = new CreateLobbyResponse(null);

        assertNull(response.getLobby());
    }
}