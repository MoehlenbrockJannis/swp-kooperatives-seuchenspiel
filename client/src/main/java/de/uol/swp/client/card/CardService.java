package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.game.Game;
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

}
