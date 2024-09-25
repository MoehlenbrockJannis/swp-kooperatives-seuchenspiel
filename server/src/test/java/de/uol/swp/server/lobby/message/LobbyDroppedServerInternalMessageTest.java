package de.uol.swp.server.lobby.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LobbyDroppedServerInternalMessage Test")
class LobbyDroppedServerInternalMessageTest {

    @Test
    @DisplayName("Should handle null lobby")
    void testNullLobby() {
        assertDoesNotThrow(() -> new LobbyDroppedServerInternalMessage(null));

        LobbyDroppedServerInternalMessage message = new LobbyDroppedServerInternalMessage(null);
        assertNull(message.getLobby());
    }
}