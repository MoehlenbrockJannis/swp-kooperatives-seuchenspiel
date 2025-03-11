package de.uol.swp.server.game;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameEndReason;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.request.LeaveGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.UpdateLobbyStatusRequest;
import de.uol.swp.common.message.server_message.ServerMessage;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
import de.uol.swp.server.communication.AISession;
import de.uol.swp.server.game.message.GameStateChangedInternalMessage;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.player.PlayerManagement;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for the game
 */
@Singleton
public class GameService extends AbstractService {

    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    private final PlayerManagement playerManagement;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ServerModule
     * @see de.uol.swp.server.di.ServerModule
     */
    @Inject
    public GameService(EventBus eventBus, GameManagement gameManagement, LobbyService lobbyService, PlayerManagement playerManagement) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
        this.playerManagement = playerManagement;
    }

    /**
     * Posts a request to create a game on the EventBus
     *
     * @param createGameRequest The request to create a game
     * @see de.uol.swp.common.game.request.CreateGameRequest
     */
    @Subscribe
    public void onCreateGameRequest(CreateGameRequest createGameRequest) {
        final Game game = gameManagement.createGame(createGameRequest.getLobby(), createGameRequest.getMapType(), createGameRequest.getPlagues(), createGameRequest.getDifficulty());
        gameManagement.addGame(game);

        final Set<Player> players = createGameRequest.getLobby().getPlayers();
        final Set<AIPlayer> aiPlayers = getAIPlayersForGame(players);

        for (AIPlayer aiPlayer : aiPlayers) {
            Session newAiPlayerSession = AISession.createAISession(aiPlayer);
            playerManagement.addAIPlayerSession(aiPlayer, newAiPlayerSession);
        }

        final CreateGameServerMessage response = new CreateGameServerMessage(game);

        response.initWithMessage(createGameRequest);
        lobbyService.sendToAllInLobby(createGameRequest.getLobby(), response);
    }

    /**
     * Creates a {@link RetrieveUpdatedGameServerMessage} with given {@link Game} and
     * sends it to all players within the associated {@link Lobby}.
     *
     * @param game the {@link Game} to update and send an update of
     * @see GameManagement#updateGame(Game)
     * @see LobbyService#sendToAllInLobby(Lobby, ServerMessage)
     */
    public void sendGameUpdate(final Game game) {
        gameManagement.updateGame(game);

        final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage = new RetrieveUpdatedGameServerMessage(game);
        lobbyService.sendToAllInLobby(game.getLobby(), retrieveUpdatedGameServerMessage);
    }

    /**
     * Returns the AI players for a game
     *
     * @param players The players in the game
     * @return The AI players in the game
     */
    private Set<AIPlayer> getAIPlayersForGame(Set<Player> players) {
        return players.stream()
                .filter(AIPlayer.class::isInstance)
                .map(AIPlayer.class::cast)
                .collect(Collectors.toSet());
    }

    /**
     * Searches the Session for a given AI Player
     *
     * @param aiPlayer AI Player whose Session is to be searched
     * @return either empty Optional or Optional containing the Session
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.player.AIPlayer
     * @since 2025-01-27
     */
    public Optional<Session> getSession(AIPlayer aiPlayer) {
        return playerManagement.findSession(aiPlayer);
    }

    /**
     * Handles a request from a player to leave the game.
     * Sets the game as lost and ends the current turn.
     *
     * @param leaveGameRequest The request containing game and player information
     */
    @Subscribe
    public void onLeaveGameRequest(LeaveGameRequest leaveGameRequest) {
        Game game = leaveGameRequest.getGame();

        if (!game.isGameLost()) {
            game.setGameLost(true);
            gameManagement.updateGame(game);
            post(new GameStateChangedInternalMessage(game, GameEndReason.PLAYER_LEFT_GAME));
        }
        gameManagement.updateGame(game);
        sendGameUpdate(game);
    }

    /**
     * Handles game state change events and processes game end conditions.
     * If the game is either won or lost, triggers the game end process.
     *
     * @param gameStateChangedInternalMessage The message containing the game and its state
     */
    @Subscribe
    public void onGameStateChanged(GameStateChangedInternalMessage gameStateChangedInternalMessage) {
        Game game = gameStateChangedInternalMessage.getGame();
        if (!game.isGameLost() && !game.isGameWon()) {
            return;
        }

        handleGameEnd(game, gameStateChangedInternalMessage.getReason());
        deleteGameAndLobby(game);
    }

    /**
     * Processes the end of a game by updating the lobby status and notifying players.
     *
     * @param game The game that has ended
     * @param reason The reason why the game ended
     */
    private void handleGameEnd(Game game, GameEndReason reason) {
        post(new UpdateLobbyStatusRequest(game.getLobby(), LobbyStatus.OVER));
        post(new SystemLobbyMessageServerInternalMessage(reason.getDisplayMessage(), game.getLobby()));
    }

    /**
     * Deletes both the game and its associated lobby from the system.
     * Sends a notification to all players that the lobby has been closed.
     *
     * @param game The game to be deleted along with its lobby
     */
    private void deleteGameAndLobby(Game game) {
        post(new SystemLobbyMessageServerInternalMessage(
                "Diese Lobby wurde automatisch geschlossen!",
                game.getLobby())
        );
        gameManagement.removeGame(game);
        lobbyService.deleteLobby(game.getLobby());
    }
}