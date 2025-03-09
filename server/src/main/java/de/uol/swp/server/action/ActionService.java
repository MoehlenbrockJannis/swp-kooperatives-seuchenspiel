package de.uol.swp.server.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.cure_plague.CurePlagueAction;
import de.uol.swp.common.action.advanced.discover_antidote.DiscoverAntidoteAction;
import de.uol.swp.common.action.advanced.transfer_card.ShareKnowledgeAction;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.card.CardService;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.message.GameStateChangedInternalMessage;
import de.uol.swp.server.player.PlayerManagement;
import de.uol.swp.server.triggerable.TriggerableService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

/**
 * The ActionService class handles the execution of actions requested by clients in the game.
 * It subscribes to action requests and processes them if valid.
 *
 * @author: Jannis Moehlenbrock
 */
@Singleton
public class ActionService extends AbstractService {
    private final CardService cardService;
    private final GameManagement gameManagement;
    private final TriggerableService triggerableService;
    private final PlayerManagement playerManagement;

    /**
     * Constructs a new ActionService with the specified EventBus and LobbyService.
     *
     * @param bus the EventBus used throughout the server
     */
    @Inject
    public ActionService(EventBus bus, CardService cardService, GameManagement gameManagement, TriggerableService triggerableService, PlayerManagement playerManagement) {
        super(bus);
        this.cardService = cardService;
        this.gameManagement = gameManagement;
        this.triggerableService = triggerableService;
        this.playerManagement = playerManagement;
    }

    /**
     * Handles action requests sent by clients. It verifies the requested action,
     * executes it if valid, and notifies all clients about the state change.
     *
     * @param request the action request from a client
     */
    @Subscribe
    public void onActionRequest(ActionRequest request) {
        final Optional<Game> gameOptional = gameManagement.getGame(request.getGame());
        if (gameOptional.isEmpty()) {
            return;
        }

        if (request.getGame().isGameLost() || request.getGame().isGameWon()) {
            return;
        }

        final Game game = gameOptional.get();
        final Player player = game.getCurrentPlayer();

        if (triggerableService.checkForSendingManualTriggerables(game, request, player)) {
            return;
        }

        final Action action = request.getAction();
        action.initWithGame(game);

        final PlayerTurn currentPlayerTurn = game.getCurrentTurn();
        currentPlayerTurn.executeCommand(action);

        gameManagement.updateGame(game);

        triggerableService.executeAutoTriggerables(game);

        RetrieveUpdatedGameServerMessage actionServerMessage = new RetrieveUpdatedGameServerMessage(game);
        actionServerMessage.initWithMessage(request);
        post(actionServerMessage);

        if (!currentPlayerTurn.isActionExecutable()) {
            cardService.allowDrawingOrDiscarding(game, request, ReleaseToDrawPlayerCardResponse.class);
        }

        checkMoveResearchLaboratory(request);
        sendChatMessageIfPlagueCubeCure(request);
        sendDiscoveredAntidoteChatMessage(request);
        evaluateDiscardingOfHandCardsFromReceiverOfShareKnowledgeActionNeedTo(request);
        checkIfAllPlaguesHaveAntidotes(request);
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
            Lobby lobby = request.getGame().getLobby();
            SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage = new SystemLobbyMessageServerInternalMessage(text, lobby);
            post(systemLobbyMessageServerInternalMessage);

            request.getGame().setRequiresTextMessageMovingResearchLaboratory(false);
        }
    }

    private void sendChatMessageIfPlagueCubeCure(ActionRequest request) {
        if(request.getAction() instanceof CurePlagueAction curePlagueAction) {
            Lobby lobby = request.getGame().getLobby();
            String text = getCurePlagueActionText(request, curePlagueAction);

            SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage = new SystemLobbyMessageServerInternalMessage(text, lobby);
            post(systemLobbyMessageServerInternalMessage);
        }
    }

    private String getCurePlagueActionText(ActionRequest request, CurePlagueAction curePlagueAction) {
        String plagueName = curePlagueAction.getPlague().getName();
        Player currentPlayer = request.getGame().getCurrentPlayer();
        Field currentField = currentPlayer.getCurrentField();
        String currentCity = currentField.getCity().getName();

        String text;
        text = "Der/Die Seuchenwürfel der Stadt " + currentCity +  " ( " + plagueName + " ) wurde/n entfernt.";
        return text;
    }

    /**
     * Sends a chat message to the system lobby when a player discovers an antidote.
     * The message includes the player's name and the name of the plague they researched.
     *
     * @param actionRequest The action request containing the game context and action details.
     * @author Marvin Tischer
     * @since 2025-02-12
     */
    private void sendDiscoveredAntidoteChatMessage(ActionRequest actionRequest) {
        if (actionRequest.getAction() instanceof DiscoverAntidoteAction antidoteAction) {
            Player currentPlayer = actionRequest.getGame().getCurrentPlayer();
            Game game = actionRequest.getGame();

            Plague plague = antidoteAction.getPlague();
            String plagueName = plague.getName();

            String text = currentPlayer + " hat das Heilmittel " + plagueName + " erforscht!";

            SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage = new SystemLobbyMessageServerInternalMessage(text, game.getLobby());
            post(systemLobbyMessageServerInternalMessage);
        }
    }

    /**
     * Evaluates whether discarding a hand card is required after executing a {@link ShareKnowledgeAction}.
     *
     * @param actionRequest {@link ActionRequest} that contains the possible {@link ShareKnowledgeAction}
     */
    private void evaluateDiscardingOfHandCardsFromReceiverOfShareKnowledgeActionNeedTo(final ActionRequest actionRequest) {
        final Action action = actionRequest.getAction();
        if (action instanceof ShareKnowledgeAction shareKnowledgeAction) {
            final Game game = action.getGame();
            final Player receiver = shareKnowledgeAction.getReceiver();
            playerManagement.findSession(receiver).ifPresent(session -> allowPlayerToDiscardHandCardIfRequired(game, receiver, session));
        }
    }

    /**
     * Allows a given {@link Player} to a discard a hand card if the requirements for that are met.
     *
     * @param game {@link Game} in which the {@link Player} is
     * @param player {@link Player} that may be allowed to discard a hand card
     * @param session {@link Session} of the {@link Player}
     * @see CardService#doesPlayerRequireDiscardingOfHandCards(Player, Game)
     */
    private void allowPlayerToDiscardHandCardIfRequired(final Game game, final Player player, final Session session) {
        if (cardService.doesPlayerRequireDiscardingOfHandCards(player, game)) {
            final RetrieveUpdatedGameServerMessage message = new RetrieveUpdatedGameServerMessage(game);
            message.setSession(session);
            message.setMessageContext(null);
            cardService.allowDrawingOrDiscarding(game, message, ReleaseToDiscardPlayerCardResponse.class);
        }
    }

    /**
     * Checks if all plagues in the game have antidotes and sets the game state to won if true.
     *
     * @param actionRequest The action request containing the game context to check for antidotes
     */
    private void checkIfAllPlaguesHaveAntidotes(final ActionRequest actionRequest) {
        if (actionRequest.getAction() instanceof DiscoverAntidoteAction antidoteAction) {
            Game game =  antidoteAction.getGame();
            Game currentGame = gameManagement.getGame(game).get();

            boolean allPlaguesHaveAntidotes = currentGame.getPlagues().stream()
                    .allMatch(currentGame::hasAntidoteMarkerForPlague);

            if (allPlaguesHaveAntidotes) {
                setGameWonIfAllPlaguesHaveAntidotes(currentGame);
            }
        }
    }

    /**
     * Sets the game state to won and ends the current turn.
     *
     * @param game The game to be marked as won
     */
    private void setGameWonIfAllPlaguesHaveAntidotes(Game game) {
        game.setGameWon(true);
        game.getCurrentTurn().setNumberOfActionsToDo(0);
        post(new GameStateChangedInternalMessage(game, gameManagement.determineGameEndReason(game)));
    }
}
