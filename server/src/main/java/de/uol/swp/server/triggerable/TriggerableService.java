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

@Singleton
public class TriggerableService extends AbstractService {
    private final GameManagement gameManagement;
    private final GameService gameService;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     * @param gameManagement {@link GameManagement} to get updated {@link Game}
     * @param gameService {@link GameService} to send game updates
     * @param lobbyService {@link LobbyService} to send messages to a lobby
     * @since 2025-01-28
     */
    @Inject
    public TriggerableService(EventBus bus, GameManagement gameManagement, GameService gameService, LobbyService lobbyService) {
        super(bus);
        this.gameManagement = gameManagement;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }


    public void executeAutoTriggerables(final Game game) {
        PlayerTurn playerTurn = game.getCurrentTurn();
        while(playerTurn.hasNextAutoTriggerable()) {
            triggerAutoTriggerables(playerTurn, game);
        }
        playerTurn.resetAutoTriggerables();
    }

    private void triggerAutoTriggerables(PlayerTurn playerTurn, final Game game) {
        AutoTriggerable autoTriggerable = playerTurn.getNextAutoTriggerable();
        autoTriggerable.initWithGame(game);
        if(autoTriggerable.isTriggered()) {
            autoTriggerable.trigger();
            gameService.sendGameUpdate(game);
        }
    }

    /**
     * Checks if the given {@link Game} has a {@link ManualTriggerable} that needs sending and does so if necessary.
     * Returns {@code true} if given {@link Game} has a {@link ManualTriggerable} to be sent, {@code false} otherwise.
     *
     * @param game {@link Game} to check the availability of {@link ManualTriggerable} for
     * @param cause {@link Message} that caused this check, to be executed after
     * @param player {@link Player} that caused this check
     * @return {@code true} if given {@link Game} has a {@link ManualTriggerable} to be sent, {@code false} otherwise
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
     * Checks whether there is a {@link ManualTriggerable} in given {@link Game}.
     *
     * @param game {@link Game} to check the availability of {@link ManualTriggerable} for
     * @return {@code true} if there is a {@link ManualTriggerable} in {@link Game}, {@code false} otherwise
     */
    private boolean isSendingOfManualTriggerableRequired(final Game game) {
        return game.getCurrentTurn().hasNextManualTriggerable();
    }

    /**
     * Sends a {@link TriggerableServerMessage} with the next available {@link ManualTriggerable}
     * from the given {@link Game} to all players in the lobby.
     *
     * @param game {@link Game} of the {@link ManualTriggerable}
     * @param cause {@link Message} to be sent after approving or rejecting the {@link ManualTriggerable}
     * @param player {@link Player} that is to send the {@link Message} {@code cause}
     * @see TriggerableServerMessage
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
     * Resets the {@link ManualTriggerable} count in the given {@link Game}'s current {@link PlayerTurn}.
     *
     * @param game the {@link Game} to reset {@link ManualTriggerable} count in
     * @see PlayerTurn#resetManualTriggerables()
     */
    private void resetManualTriggerables(final Game game) {
        game.getCurrentTurn().resetManualTriggerables();
    }

    /**
     * Handles a {@link TriggerableRequest} detected on the {@link EventBus} by triggering the contained {@link Triggerable}.
     * If the {@link Triggerable} is an {@link EventCard}, it is discarded.
     * After all the necessary measures, the {@link Game} is updated and a {@link AnswerableServerMessage} is sent back.
     *
     * @param triggerableRequest {@link TriggerableRequest} with {@link Triggerable} to trigger
     */
    @Subscribe
    public void onTriggerableRequest(final TriggerableRequest triggerableRequest) {
        final Triggerable triggerable = triggerableRequest.getTriggerable();

        final Optional<Game> gameOptional = gameManagement.getGame(triggerable.getGame());
        if(gameOptional.isEmpty()) {
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
     * @param eventCard {@link EventCard} to discard
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
     * Sends an {@link AnswerableServerMessage} for given {@link TriggerableRequest}.
     *
     * @param triggerableRequest {@link TriggerableRequest} to send an {@link AnswerableServerMessage} for
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
