package de.uol.swp.server.triggerable;

import com.google.inject.Inject;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
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
     * Sends an {@link ApprovableServerMessage} with the next available {@link ManualTriggerable}
     * from the given {@link Game} to all players in the lobby.
     * Sets a {@link TriggerableRequest} as the {@code onApproved} {@link Message} of the {@link ApprovableServerMessage}
     * to be sent after executing the {@link ManualTriggerable}.
     *
     * @param game {@link Game} of the {@link ManualTriggerable}
     * @param cause {@link Message} to be sent after approving or rejecting the {@link ManualTriggerable}
     * @param player {@link Player} that is to send the {@link Message} {@code cause}
     * @see TriggerableRequest
     * @see ApprovableServerMessage
     */
    private void sendNextManualTriggerable(final Game game, final Message cause, final Player player) {
        final ManualTriggerable manualTriggerable = game.getCurrentTurn().getNextManualTriggerable();

        gameService.sendGameUpdate(game);

        final TriggerableRequest triggerableRequest = new TriggerableRequest(manualTriggerable, cause, player);

        final ApprovableServerMessage approvableServerMessage = new ApprovableServerMessage(
                ApprovableMessageStatus.OUTBOUND,
                manualTriggerable,
                triggerableRequest,
                manualTriggerable.getPlayer(),
                cause,
                player
        );
        lobbyService.sendToAllInLobby(game.getLobby(), approvableServerMessage);
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
     * After all the necessary measures, the {@link Game} is updated and a {@link TriggerableServerMessage} is sent back.
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

        triggerable.trigger();

        gameService.sendGameUpdate(game);

        if (triggerable instanceof EventCard eventCard) {
            discardEventCard(eventCard, triggerableRequest);
        }

        final TriggerableServerMessage triggerableServerMessage = new TriggerableServerMessage(
                triggerable,
                triggerableRequest.getCause(),
                triggerableRequest.getReturningPlayer()
        );
        triggerableServerMessage.initWithMessage(triggerableRequest);
        post(triggerableServerMessage);
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
}
