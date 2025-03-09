package de.uol.swp.server.game.turn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.game.turn.request.EndPlayerTurnRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Service for player turns
 *
 * @see PlayerTurn
 *
 * @author Silas van Thiel
 * @since 2025-01-20
 */
@Singleton
public class PlayerTurnService extends AbstractService {

    private final PlayerTurnManagement playerTurnManagement;
    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    private static final Logger LOG = LogManager.getLogger(PlayerTurnService.class);

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
        try {
            Game currentGame = request.getGame();

            if (currentGame.isGameLost() || currentGame.isGameWon()) {
                return;
            }

            playerTurnManagement.startNewPlayerTurn(currentGame);
            gameManagement.updateGame(currentGame);

            RetrieveUpdatedGameServerMessage message = new RetrieveUpdatedGameServerMessage(currentGame);
            Lobby currentLobby = currentGame.getLobby();
            lobbyService.sendToAllInLobby(currentLobby, message);
        } catch (IllegalArgumentException e) {
            LOG.info(e.getMessage());
        }
    }
}
