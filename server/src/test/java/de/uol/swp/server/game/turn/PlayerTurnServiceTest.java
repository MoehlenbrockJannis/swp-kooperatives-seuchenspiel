package de.uol.swp.server.game.turn;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.game.turn.request.EndPlayerTurnRequest;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        Lobby lobby = new LobbyDTO("TestLobby", null);
        when(game.getLobby()).thenReturn(lobby);

        EndPlayerTurnRequest request = new EndPlayerTurnRequest(game);

        gameManagement.addGame(game);

        playerTurnService.onEndPlayerTurnRequest(request);

        verify(playerTurnManagement, times(1)).startNewPlayerTurn(game);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(lobby), any(RetrieveUpdatedGameServerMessage.class));
    }
}