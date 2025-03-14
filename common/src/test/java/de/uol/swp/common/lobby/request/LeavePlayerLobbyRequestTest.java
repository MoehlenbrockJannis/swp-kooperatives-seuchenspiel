package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LobbyLeaveUserRequest Test")
class LeavePlayerLobbyRequestTest {

    private UserDTO user;
    private Player player;
    private LobbyDTO lobby;

    @BeforeEach
    void setUp() {
        this.user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
        this.player = new UserPlayer(this.user);
        this.lobby = new LobbyDTO("TestLobby", this.user);
    }

    @Test
    @DisplayName("Create LobbyLeaveUserRequest")
    void createLobbyLeaveUserRequest() {
        LeavePlayerLobbyRequest request = new LeavePlayerLobbyRequest(lobby, player);

        assertEquals(lobby, request.getLobby());
        assertEquals(player, request.getPlayer());
    }
}