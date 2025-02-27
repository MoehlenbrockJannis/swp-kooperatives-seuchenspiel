package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.request.DiscardInfectionCardRequest;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.request.RequestMessage;
import de.uol.swp.common.player.Player;
import org.greenrobot.eventbus.EventBus;

/**
 * Service class for handling card-related operations.
 */
public class CardService {

    private final EventBus eventBus;

    /**
     * Constructs a new CardService with the specified EventBus.
     *
     * @param eventBus the EventBus to be used for posting events
     */
    @Inject
    public CardService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Sends a request to draw a player card.
     * <p>
     * This method creates a {@link DrawPlayerCardRequest} with the specified game and player,
     * and posts it to the {@link EventBus}.
     * </p>
     *
     * @param game   The game from which to draw the player card
     * @param player The player who is drawing the card
     * @since 2024-09-20
     */
    public void sendDrawPlayerCardRequest(Game game, Player player) {
        DrawPlayerCardRequest drawPlayerCardRequest = new DrawPlayerCardRequest(game, player);
        eventBus.post(drawPlayerCardRequest);
    }

    /**
     * Sends a request to discard a player card.
     * <p>
     * This method creates a {@link DiscardPlayerCardRequest} with the specified game, player, and player card,
     * and posts it to the {@link EventBus}.
     * </p>
     *
     * @param game       The game from which to discard the player card
     * @param player     The player who is discarding the card
     * @param playerCard The player card to be discarded
     * @since 2024-09-20
     */
    public void sendDiscardPlayerCardRequest(Game game, Player player, PlayerCard playerCard) {
        RequestMessage discardPlayerCardRequest = new DiscardPlayerCardRequest<>(game, player, playerCard);
        eventBus.post(discardPlayerCardRequest);
    }

    /**
     * Sends a request to draw an infection card.
     * <p>
     * This method creates a {@link DrawInfectionCardRequest} with the specified game and player,
     * and posts it to the {@link EventBus}.
     * </p>
     *
     * @param game   The game from which to draw the infection card
     * @param player The player who is drawing the card
     */
    public void sendDrawInfectionCardRequest(Game game, Player player) {
        DrawInfectionCardRequest drawInfectionCardRequest = new DrawInfectionCardRequest(game, player);
        eventBus.post(drawInfectionCardRequest);
    }

    /**
     * Sends a request to discard an infection card.
     * <p>
     * This method creates a {@link DiscardInfectionCardRequest} with the specified game, player, and infection card,
     * and posts it to the {@link EventBus}.
     * </p>
     *
     * @param game          The game from which to discard the infection card
     * @param player        The player who is discarding the card
     * @param infectionCard The infection card to be discarded
     */
    public void sendDiscardInfectionCardRequest(Game game, Player player, InfectionCard infectionCard) {
        DiscardInfectionCardRequest discardInfectionCardRequest = new DiscardInfectionCardRequest(game, player, infectionCard);
        eventBus.post(discardInfectionCardRequest);
    }
}
