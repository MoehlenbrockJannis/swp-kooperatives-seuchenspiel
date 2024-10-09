package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LobbyUpdateStatusRequest Test")
class LobbyUpdateStatusRequestTest {

    private Lobby testLobby;
    private LobbyStatus testStatus;

    @BeforeEach
    void setUp() {
        UserDTO owner = new UserDTO("owner", "password", "owner@example.com");
        testLobby = new LobbyDTO("TestLobby", owner, 2, 4);
        testStatus = LobbyStatus.RUNNING;
    }

    @Test
    @DisplayName("Constructor should accept null lobby without throwing exception")
    void nullLobbyTest() {
        assertDoesNotThrow(() -> new UpdateLobbyStatusRequest(null, testStatus));
    }

    @Test
    @DisplayName("Constructor should allow null status")
    void nullStatusTest() {
        UpdateLobbyStatusRequest request = new UpdateLobbyStatusRequest(testLobby, null);

        assertNotNull(request.getLobby());
        assertNull(request.getStatus());
        assertEquals(testLobby, request.getLobby());
    }
}