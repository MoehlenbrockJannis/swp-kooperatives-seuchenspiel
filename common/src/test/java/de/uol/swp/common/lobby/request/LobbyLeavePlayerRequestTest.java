package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the lobby leave user request
 *
 * @see LeavePlayerLobbyRequest
 * @since 2023-05-14
 */
@DisplayName("LobbyLeaveUserRequest Test")
class LobbyLeavePlayerRequestTest {

    private UserDTO user;
    private Player player;
    private LobbyDTO lobby;

    @BeforeEach
    void setUp() {
        this.user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
        this.player = new UserPlayer(this.user);
        this.lobby = new LobbyDTO("TestLobby", this.user);
    }

    /**
     * Test for creation of the LobbyLeaveUserRequests
     *
     * This test checks if the lobbyName and the user of the LobbyLeaveUserRequest gets
     * set correctly during the creation of the request
     *
     * @since 2023-05-14
     */
    @Test
    @DisplayName("Create LobbyLeaveUserRequest")
    void createLobbyLeaveUserRequest() {
        LeavePlayerLobbyRequest request = new LeavePlayerLobbyRequest(lobby, player);

        assertEquals(lobby, request.getLobby());
        assertEquals(player, request.getPlayer());
    }
}