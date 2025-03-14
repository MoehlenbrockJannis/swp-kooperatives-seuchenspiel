package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserLeftLobbyMessage Test")
class LeavePlayerLobbyServerMessageTest {

    private UserDTO user;
    private Player player;
    private LobbyDTO lobby;

    @BeforeEach
    void setUp() {
        this.user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
        this.lobby = new LobbyDTO("TestLobby", this.user);
        this.player = new UserPlayer(this.user);
    }

    @Test
    @DisplayName("Create UserLeftLobbyServerMessage")
    void createUserLeftLobbyMessage() {
        LeavePlayerLobbyServerMessage message = new LeavePlayerLobbyServerMessage(lobby, player);

        assertEquals(lobby, message.getLobby());
        assertEquals(player, message.getPlayer());
    }
}