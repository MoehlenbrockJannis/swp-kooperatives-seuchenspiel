package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for the game
 */

public class GameService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     */
    @Inject
    public GameService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Posts a request to create a game on the EventBus
     *
     * @param lobby The lobby from which the game is to be created
     * @see de.uol.swp.common.game.request.CreateGameRequest
     */
    public void createGame(Lobby lobby) {
        //TODO: replace null with the correct configuration parameters
        List<Player> userPlayers = createUserPlayersList(lobby);
        CreateGameRequest createGameRequest = new CreateGameRequest(lobby, null, userPlayers, null);
        eventBus.post(createGameRequest);
    }

    private List<Player> createUserPlayersList(Lobby lobby) {
        List<Player> userPlayers = new ArrayList<>();
        lobby.getUsers().forEach(user -> userPlayers.add(new UserPlayer(null, user)));
        return userPlayers;
    }
}
