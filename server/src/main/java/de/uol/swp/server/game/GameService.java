package de.uol.swp.server.game;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server.ServerMessage;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.communication.AISession;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for the game
 */
@Singleton
public class GameService extends AbstractService {

    private final Map<Session, AIPlayer> aiPlayerSessions = new HashMap<>();

    private final GameManagement gameManagement;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ServerModule
     * @see de.uol.swp.server.di.ServerModule
     */
    @Inject
    public GameService(EventBus eventBus, GameManagement gameManagement, LobbyService lobbyService) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
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
            aiPlayerSessions.put(newAiPlayerSession, aiPlayer);
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
                .filter(player -> player instanceof AIPlayer)
                .map(player -> (AIPlayer) player)
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
        Optional<Map.Entry<Session, AIPlayer>> entry = aiPlayerSessions.entrySet().stream().filter(e -> e.getValue().equals(aiPlayer)).findFirst();
        return entry.map(Map.Entry::getKey);
    }
}