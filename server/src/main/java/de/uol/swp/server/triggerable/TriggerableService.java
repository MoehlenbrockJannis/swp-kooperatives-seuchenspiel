package de.uol.swp.server.triggerable;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.answerable.server_message.AnswerableServerMessage;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
import de.uol.swp.common.triggerable.server_message.TriggerableServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

/**
 * Service class for managing triggerable items in the game.
 * <p>
 * The {@code TriggerableService} listens to events related to triggerables on the {@link EventBus},
 * handles automatic and manual triggers, manages triggerable execution within games, and interacts with
 * other services such as {@link GameService} and {@link LobbyService}.
 * </p>
 */
@Singleton
public class TriggerableService extends AbstractService {

    private final GameManagement gameManagement;
    private final GameService gameService;
    private final LobbyService lobbyService;

    /**
     * Constructs a new {@code TriggerableService}.
     *
     * @param bus            The {@link EventBus} used to listen for and post events.
     * @param gameManagement The {@link GameManagement} instance for accessing game states.
     * @param gameService    The {@link GameService} instance for sending game updates.
     * @param lobbyService   The {@link LobbyService} instance for sending messages within lobbies.
     * @since 2025-01-28
     */
    @Inject
    public TriggerableService(EventBus bus, GameManagement gameManagement, GameService gameService, LobbyService lobbyService) {
        super(bus);
        this.gameManagement = gameManagement;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }

    /**
     * Executes all automatic triggerable in the game until none remain.
     *
     * @param game The {@link Game} in which to execute automatic triggerables.
     */
    public void executeAutoTriggerables(final Game game) {
        PlayerTurn playerTurn = game.getCurrentTurn();
        while (playerTurn.hasNextAutoTriggerable()) {
            triggerAutoTriggerables(playerTurn, game);
        }
        playerTurn.resetAutoTriggerables();
    }

    /**
     * Triggers the next automatic triggerable in the player's turn.
     *
     * @param playerTurn The current {@link PlayerTurn}.
     * @param game       The {@link Game} instance.
     */
    private void triggerAutoTriggerables(PlayerTurn playerTurn, final Game game) {
        AutoTriggerable autoTriggerable = playerTurn.getNextAutoTriggerable();
        autoTriggerable.initWithGame(game);
        if (autoTriggerable.isTriggered()) {
            autoTriggerable.trigger();
            gameService.sendGameUpdate(game);
        }
    }

    /**
     * Checks if a manual triggerable is available in the game and handles sending it.
     *
     * @param game   The {@link Game} to check for manual triggerable.
     * @param cause  The {@link Message} that caused the check, to be executed later.
     * @param player The {@link Player} initiating the check.
     * @return {@code true} if a manual triggerable is available and sent, {@code false} otherwise.
     */
    public boolean checkForSendingManualTriggerables(final Game game, final Message cause, final Player player) {
        if (isSendingOfManualTriggerableRequired(game)) {
            sendNextManualTriggerable(game, cause, player);
            return true;
        } else {
            resetManualTriggerables(game);
            return false;
        }
    }

    /**
     * Checks whether there is a manual triggerable available in the current game turn.
     *
     * @param game The {@link Game} instance.
     * @return {@code true} if a manual triggerable is available, {@code false} otherwise.
     */
    private boolean isSendingOfManualTriggerableRequired(final Game game) {
        return game.getCurrentTurn().hasNextManualTriggerable();
    }

    /**
     * Sends the next available manual triggerable in the current game turn.
     *
     * @param game   The {@link Game} instance.
     * @param cause  The {@link Message} to execute after the triggerable is processed.
     * @param player The {@link Player} initiating the triggerable.
     */
    private void sendNextManualTriggerable(final Game game, final Message cause, final Player player) {
        final ManualTriggerable manualTriggerable = game.getCurrentTurn().getNextManualTriggerable();

        gameService.sendGameUpdate(game);

        final TriggerableServerMessage triggerableServerMessage = new TriggerableServerMessage(
                manualTriggerable,
                cause,
                player
        );
        lobbyService.sendToAllInLobby(game.getLobby(), triggerableServerMessage);
    }

    /**
     * Resets the manual triggerable in the current game turn.
     *
     * @param game The {@link Game} instance.
     */
    private void resetManualTriggerables(final Game game) {
        game.getCurrentTurn().resetManualTriggerables();
    }

    /**
     * Handles {@link TriggerableRequest} events found on the {@link EventBus}.
     * <p>
     * Processes the triggerable contained in the request, updates the game state, and sends appropriate
     * responses. If the triggerable is associated with an {@link EventCard}, the card is discarded.
     * </p>
     *
     * @param triggerableRequest The {@link TriggerableRequest} received on the EventBus.
     */
    @Subscribe
    public void onTriggerableRequest(final TriggerableRequest triggerableRequest) {
        final Triggerable triggerable = triggerableRequest.getTriggerable();

        final Optional<Game> gameOptional = gameManagement.getGame(triggerable.getGame());
        if (gameOptional.isEmpty()) {
            return;
        }
        final Game game = gameOptional.get();
        triggerable.initWithGame(game);

        PlayerTurn playerTurn = game.getCurrentTurn();
        playerTurn.executeCommand(triggerable);

        gameService.sendGameUpdate(game);

        if (triggerable instanceof EventCard eventCard) {
            discardEventCard(eventCard, triggerableRequest);
        }

        sendAnswerableServerMessage(triggerableRequest);
    }

    /**
     * Discards the given {@link EventCard}.
     *
     * @param eventCard                The {@link EventCard} to discard.
     * @param originOfDiscardingPlayer The {@link Message} responsible for initiating the discard.
     */
    private void discardEventCard(final EventCard eventCard, final Message originOfDiscardingPlayer) {
        final DiscardPlayerCardRequest<EventCard> discardPlayerCardRequest = new DiscardPlayerCardRequest<>(
                eventCard.getGame(),
                eventCard.getPlayer(),
                eventCard
        );
        discardPlayerCardRequest.initWithMessage(originOfDiscardingPlayer);
        post(discardPlayerCardRequest);
    }

    /**
     * Sends an {@link AnswerableServerMessage} for the provided {@link TriggerableRequest}.
     *
     * @param triggerableRequest The {@link TriggerableRequest} to respond to.
     */
    private void sendAnswerableServerMessage(final TriggerableRequest triggerableRequest) {
        final Triggerable triggerable = triggerableRequest.getTriggerable();
        final Message cause = triggerableRequest.getCause();
        final Player returningPlayer = triggerableRequest.getReturningPlayer();

        if (triggerable instanceof ManualTriggerable manualTriggerable) {
            final AnswerableServerMessage answerableServerMessage = new AnswerableServerMessage(
                    manualTriggerable,
                    cause,
                    returningPlayer
            );
            answerableServerMessage.initWithMessage(triggerableRequest);
            post(answerableServerMessage);
        }
    }
}