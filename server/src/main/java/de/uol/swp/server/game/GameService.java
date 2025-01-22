package de.uol.swp.server.game;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Service for the game
 */
@Singleton
public class GameService extends AbstractService {

    private final GameManagement gameManagement;
    private final AuthenticationService authenticationService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ServerModule
     * @see de.uol.swp.server.di.ServerModule
     */
    @Inject
    public GameService(EventBus eventBus, AuthenticationService authenticationService, GameManagement gameManagement) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.authenticationService = authenticationService;
    }

    /**
     * Posts a request to create a game on the EventBus
     *
     * @param createGameRequest The request to create a game
     * @see de.uol.swp.common.game.request.CreateGameRequest
     */
    @Subscribe
    public void onCreateGameRequest(CreateGameRequest createGameRequest) {
        final Game game = gameManagement.createGame(createGameRequest.getLobby(), createGameRequest.getMapType(), createGameRequest.getPlagues(), createGameRequest.getNumberOfEpidemicCards());
        gameManagement.addGame(game);

        final CreateGameServerMessage response = new CreateGameServerMessage(game);

        response.initWithMessage(createGameRequest);
        response.setReceiver(authenticationService.getSessions(createGameRequest.getLobby().getUsers()));
        post(response);
    }
}