package de.uol.swp.server.action;

import com.google.inject.Inject;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.card.CardService;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
import de.uol.swp.server.game.GameManagement;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

/**
 * The ActionService class handles the execution of actions requested by clients in the game.
 * It subscribes to action requests and processes them if valid.
 *
 * @author: Jannis Moehlenbrock
 */

public class ActionService extends AbstractService {
    private final CardService cardService;
    private final GameManagement gameManagement;

    /**
     * Constructs a new ActionService with the specified EventBus and LobbyService.
     *
     * @param bus the EventBus used throughout the server
     */
    @Inject
    public ActionService(EventBus bus, CardService cardService, GameManagement gameManagement) {
        super(bus);
        this.cardService = cardService;
        this.gameManagement = gameManagement;
    }

    /**
     * Handles action requests sent by clients. It verifies the requested action,
     * executes it if valid, and notifies all clients about the state change.
     *
     * @param request the action request from a client
     */
    @Subscribe
    public void onActionRequest(ActionRequest request) {
        final Game game = request.getGame();
        final PlayerTurn currentPlayerTurn = game.getCurrentTurn();
        currentPlayerTurn.executeCommand(request.getAction());

        gameManagement.updateGame(game);

        RetrieveUpdatedGameServerMessage actionServerMessage = new RetrieveUpdatedGameServerMessage(request.getGame());
        actionServerMessage.initWithMessage(request);
        post(actionServerMessage);

        final Optional<Session> requestSessionOptional = request.getSession();
        if (!currentPlayerTurn.areActionExecutable() && requestSessionOptional.isPresent()) {
            cardService.allowDrawingOrDiscarding(game, requestSessionOptional.get(), ReleaseToDrawPlayerCardResponse.class);
        }

        checkMoveResearchLaboratory(request);
    }

    /**
     * Checks if the move of a research laboratory can proceed. If the maximum number of research laboratories
     * has been reached and a text message is required, it sends a system message indicating this status to the lobby.
     *
     * @param request the {@link ActionRequest} object containing the details of the requested action
     */
    private void checkMoveResearchLaboratory(ActionRequest request) {
        if(request.getAction().getGame().getResearchLaboratories().size() >= Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES &&
                request.getGame().isRequiresTextMessageMovingResearchLaboratory()) {
            String text = "Die maximale Anzahl an Forschungslaboren wurde erreicht!";
            SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage = new SystemLobbyMessageServerInternalMessage(text, request.getGame().getLobby());
            post(systemLobbyMessageServerInternalMessage);

            request.getGame().setRequiresTextMessageMovingResearchLaboratory(false);
        }
    }
}
