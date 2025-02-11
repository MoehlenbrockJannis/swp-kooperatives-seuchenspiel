package de.uol.swp.server.player.turn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.turn.request.EndPlayerTurnRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Service for player turns
 *
 * @see de.uol.swp.common.player.turn.PlayerTurn
 *
 * @author Silas van Thiel
 * @since 2025-01-20
 */
@Singleton
public class PlayerTurnService extends AbstractService {

    private final PlayerTurnManagement playerTurnManagement;
    private final GameManagement gameManagement;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     * @param playerTurnManagement the management for player turns
     * @param gameManagement the management for games
     * @param lobbyService the service for lobbies
     * @since 2025-01-20
     */
    @Inject
    public PlayerTurnService(EventBus bus, PlayerTurnManagement playerTurnManagement, GameManagement gameManagement, LobbyService lobbyService) {
        super(bus);
        this.playerTurnManagement = playerTurnManagement;
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Handles a {@link EndPlayerTurnRequest} from a client.
     * This starts a new player turn and sends the updated game to all clients in the lobby.
     *
     * @param request the request to end the player turn
     * @since 2025-01-20
     */
    @Subscribe
    public void onEndPlayerTurnRequest(EndPlayerTurnRequest request) {
        Game currentGame = request.getGame();
        playerTurnManagement.startNewPlayerTurn(currentGame);
        gameManagement.updateGame(currentGame);

        RetrieveUpdatedGameServerMessage message = new RetrieveUpdatedGameServerMessage(request.getGame());
        Lobby currentLobby = currentGame.getLobby();
        lobbyService.sendToAllInLobby(currentLobby, message);
    }
}
