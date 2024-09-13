package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.lobby.Lobby;
import org.greenrobot.eventbus.EventBus;

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
        CreateGameRequest createGameRequest = new CreateGameRequest(lobby, null, null);
        eventBus.post(createGameRequest);
    }
}
