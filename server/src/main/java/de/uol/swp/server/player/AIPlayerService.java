package de.uol.swp.server.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.simple.car.CarAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.game.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import de.uol.swp.common.triggerable.server_message.TriggerableServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for AI Players
 *
 * @see de.uol.swp.common.player.AIPlayer
 *
 * @author Silas van Thiel
 * @since 2025-01-28
 */
@Singleton
public class AIPlayerService extends AbstractService {

    public static final int SCHEDULER_CORE_POOL_SIZE = 1;
    public static final int AI_PLAYER_SCHEDULER_DELAY = 2;

    private final GameService gameService;
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor of the AIPlayerService
     *
     * @param bus          The event bus
     * @param gameService  The game service
     */
    @Inject
    public AIPlayerService(EventBus bus, GameService gameService) {
        super(bus);
        this.gameService = gameService;
        this.scheduler = Executors.newScheduledThreadPool(SCHEDULER_CORE_POOL_SIZE);
    }

    /**
     * Handles the response of the game creation
     *
     * @param message The message that triggered the method
     */
    @Subscribe
    public void onCreateGameServerMessage(CreateGameServerMessage message) {
        final Game game = message.getGame();
        final PlayerTurn playerTurn = game.getCurrentTurn();

        if (isAIPlayerTurn(playerTurn)) {
            handleAIPlayerTurnProcess(game);
        }
    }

    /**
     * Handles game updates from the server, updating the game state as necessary.
     *
     * @param message the message containing the updated game state from the server
     */
    @Subscribe
    public void onRetrieveUpdatedGameServerMessage(final RetrieveUpdatedGameServerMessage message) {
        final Game game = message.getGame();
        final PlayerTurn playerTurn = game.getCurrentTurn();

        if (isAIPlayerTurn(playerTurn)) {

            endTurnIfOver(playerTurn, game);

            handleAIPlayerTurnProcess(game);
        }
    }

    /**
     * Ends the player's turn for the AI player if it is over.
     *
     * @param playerTurn the current player's turn
     * @param game the current game
     */
    private void endTurnIfOver(PlayerTurn playerTurn, Game game) {
        if (playerTurn.isOver()) {
            EndPlayerTurnRequest endPlayerTurnRequest = new EndPlayerTurnRequest(game);
            post(endPlayerTurnRequest);
        }
    }

    /**
     * Handles the response of the release to discard player card
     *
     * @param message The message that triggered the method
     */
    @Subscribe
    public void onReceiveReleaseToDiscardPlayerCardResponse(ReleaseToDiscardPlayerCardResponse message) {
        final Game game = message.getGame();

        if (game.isGameLost() || game.isGameWon()) {
            return;
        }

        final PlayerTurn playerTurn = game.getCurrentTurn();

        if (isAIPlayerTurn(playerTurn)) {
            final Player currentPlayer = game.getCurrentPlayer();
            final List<PlayerCard> currentPlayerHandCards = currentPlayer.getHandCards();

            final int randomIndex = getRandomIndexOffList(currentPlayerHandCards);

            final DiscardPlayerCardRequest<PlayerCard> discardPlayerCardRequest = new DiscardPlayerCardRequest<>(game, currentPlayer, currentPlayerHandCards.get(randomIndex));

            post(discardPlayerCardRequest);
        }
    }

    /**
     * Handles the response of the approvable server message
     *
     * @param message The message that triggered the method
     */
    @Subscribe
    public void onApprovableServerMessage(final ApprovableServerMessage message) {
        final Approvable approvable = message.getApprovable();
        final Player approvingPlayer = approvable.getApprovingPlayer();
        final ApprovableMessageStatus approvableMessageStatus = message.getStatus();

        if (!(approvingPlayer instanceof AIPlayer)) {
            return;
        }

        if (approvableMessageStatus == ApprovableMessageStatus.OUTBOUND) {
            approvable.approve();
            final ApprovableRequest approvableRequest = new ApprovableRequest(
                    ApprovableMessageStatus.APPROVED,
                    approvable,
                    message.getOnApproved(),
                    message.getOnApprovedPlayer(),
                    message.getOnRejected(),
                    message.getOnRejectedPlayer()
            );
            post(approvableRequest);
        } else {
            answerSendMessageByPlayerServerMessageIfRequirementsAreMet(message);
        }
    }

    /**
     * Handles the response of a {@link TriggerableServerMessage}
     *
     * @param message the {@link TriggerableServerMessage} to respond to
     */
    @Subscribe
    public void onTriggerableServerMessage(final TriggerableServerMessage message) {
        answerSendMessageByPlayerServerMessageIfRequirementsAreMet(message);
    }

    /**
     * Answers a given {@link SendMessageByPlayerServerMessage}
     * if it is to be answered by an {@link AIPlayer} and the {@link Message} to send is not {@code null}.
     *
     * @param serverMessage {@link SendMessageByPlayerServerMessage} to answer
     */
    private void answerSendMessageByPlayerServerMessageIfRequirementsAreMet(final SendMessageByPlayerServerMessage serverMessage) {
        final Player returningPlayer = serverMessage.getReturningPlayer();
        final Message message = serverMessage.getMessageToSend();
        if (returningPlayer instanceof AIPlayer aiPlayer && message != null) {
            final Optional<Session> sessionOptional = gameService.getSession(aiPlayer);
            sessionOptional.ifPresent(message::setSession);
            post(message);
        }
    }

    /**
     * Checks if the current player is an AI player
     *
     * @param playerTurn The current player turn
     * @return True if the current player is an AI player, false otherwise
     */
    public boolean isAIPlayerTurn(PlayerTurn playerTurn) {
        final Player currentPlayer = playerTurn.getPlayer();
        return currentPlayer.getClass().isAssignableFrom(AIPlayer.class);
    }

    /**
     * Returns a random index of a list
     *
     * @param list The list
     * @return The random index
     */
    private <T> int getRandomIndexOffList(List<T> list) {
        return (int) (Math.random() * list.size());
    }

    /**
     * Handles the AI player turn process
     *
     * @param game The current game
     */
    public void handleAIPlayerTurnProcess(Game game) {
        if (game.isGameLost() || game.isGameWon()) {
            return;
        }

        final PlayerTurn currentTurn = game.getCurrentTurn();
        final Player currentPlayer = currentTurn.getPlayer();

        scheduler.schedule(() -> {
            if (!game.isGameWon() && !game.isGameLost() && currentTurn.isInInfectionCardDrawPhase()) {
                handleAIInfectionCardDrawPhase(game, currentPlayer);
            }
        }, AI_PLAYER_SCHEDULER_DELAY, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            if (!game.isGameWon() && !game.isGameLost() && currentTurn.isInPlayerCardDrawPhase()) {
                handleAIPlayerCardDrawPhase(game, currentPlayer);
            }
        }, AI_PLAYER_SCHEDULER_DELAY, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            if (!game.isGameWon() && !game.isGameLost() && currentTurn.isInActionPhase()) {
                handleAIPlayerActionPhase(game, currentPlayer);
            }
        }, AI_PLAYER_SCHEDULER_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Handles the AI player action phase
     *
     * @param game The current game
     * @param currentPlayer The current player
     */
    protected void handleAIPlayerActionPhase(Game game, Player currentPlayer) {
        if (game.isGameLost() || game.isGameWon()) {
            return;
        }

        final Field currentField = currentPlayer.getCurrentField();
        final List<Field> neighborFields = currentField.getNeighborFields();
        final int randomIndex = getRandomIndexOffList(neighborFields);

        final CarAction carAction = createAIPlayerCarAction(neighborFields, randomIndex, currentPlayer, game);
        final ActionRequest request = new ActionRequest(game, carAction);
        post(request);
    }

    /**
     * Creates a car action for the AI player
     *
     * @param neighborFields The neighbor fields of the current field
     * @param randomIndex The random index
     * @param currentPlayer The current player
     * @param game The current game
     * @return The car action
     */
    private CarAction createAIPlayerCarAction(List<Field> neighborFields, int randomIndex, Player currentPlayer, Game game) {
        final CarAction carAction = new CarAction();

        carAction.setTargetField(neighborFields.get(randomIndex));
        carAction.setExecutingPlayer(currentPlayer);
        carAction.setGame(game);

        return carAction;
    }

    /**
     * Handles the AI player card draw phase
     *
     * @param game The current game
     * @param currentPlayer The current player
     */
    protected void handleAIPlayerCardDrawPhase(Game game, Player currentPlayer) {
        if (game.isGameLost() || game.isGameWon()) {
            return;
        }

        final DrawPlayerCardRequest message = new DrawPlayerCardRequest(game, currentPlayer);
        final Session aiPlayerSession = getAIPlayerSession(currentPlayer);
        message.setSession(aiPlayerSession);
        post(message);
    }

    /**
     * Handles the AI infection card draw phase
     *
     * @param game The current game
     * @param currentPlayer The current player
     */
    protected void handleAIInfectionCardDrawPhase(Game game, Player currentPlayer) {
        if (game.isGameLost() || game.isGameWon()) {
            return;
        }

        final DrawInfectionCardRequest message = new DrawInfectionCardRequest(game, currentPlayer);
        final Session aiPlayerSession = getAIPlayerSession(currentPlayer);
        message.setSession(aiPlayerSession);
        post(message);
    }

    /**
     * Returns the session of the AI player
     *
     * @param currentPlayer The current player
     * @return The session of the AI player
     */
    private Session getAIPlayerSession(Player currentPlayer) {
        final Optional<Session> aiPlayerSession = gameService.getSession((AIPlayer) currentPlayer);
        return aiPlayerSession.orElseThrow();
    }
}