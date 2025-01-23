package de.uol.swp.server.player.turn;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.player.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PlayerTurnServiceTest {
    private PlayerTurnManagement playerTurnManagement;
    private GameManagement gameManagement;
    private LobbyService lobbyService;

    private PlayerTurnService playerTurnService;

    @BeforeEach
    void setUp() {
        EventBus bus = mock(EventBus.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        gameManagement = mock(GameManagement.class);
        lobbyService = mock(LobbyService.class);
        playerTurnService = new PlayerTurnService(bus, playerTurnManagement, gameManagement, lobbyService);
    }

    @Test
    @DisplayName("End player turn request")
    void onEndPlayerTurnRequestTest() {
        Game game = mock(Game.class);

        Lobby lobby = new LobbyDTO("TestLobby", null, 2, 4);
        when(game.getLobby()).thenReturn(lobby);

        EndPlayerTurnRequest request = new EndPlayerTurnRequest(game);

        gameManagement.addGame(game);

        playerTurnService.onEndPlayerTurnRequest(request);

        verify(playerTurnManagement, times(1)).startNewPlayerTurn(game);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(lobby), any(RetrieveUpdatedGameServerMessage.class));
    }
}