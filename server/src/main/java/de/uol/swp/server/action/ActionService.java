package de.uol.swp.server.action;

import com.google.inject.Inject;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.server_message.ActionServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * The ActionService class handles the execution of actions requested by clients in the game.
 * It subscribes to action requests and processes them if valid.
 *
 * @author: Jannis Moehlenbrock
 */

public class ActionService extends AbstractService {

    private final LobbyService lobbyService;

    /**
     * Constructs a new ActionService with the specified EventBus and LobbyService.
     *
     * @param bus the EventBus used throughout the server
     * @param lobbyService the LobbyService used for managing lobbies
     */
    @Inject
    public ActionService(EventBus bus, LobbyService lobbyService) {
        super(bus);
        this.lobbyService = lobbyService;
    }

    /**
     * Handles action requests sent by clients. It verifies the requested action,
     * executes it if valid, and notifies all clients about the state change.
     *
     * @param request the action request from a client
     */
    @Subscribe
    public void onActionRequest(ActionRequest request) {
        request.getAction().execute();

        ActionServerMessage actionServerMessage = new ActionServerMessage(request.getGame());
        actionServerMessage.initWithMessage(request);
        post(actionServerMessage);

        //TODO PlayerTurn auf nächsten Spieler für das Game setzen.... sobald vier Aktionen durchgeführt wurden beziehungsweise einmal eine WaiveAction verwendet wurde.
    }
}
