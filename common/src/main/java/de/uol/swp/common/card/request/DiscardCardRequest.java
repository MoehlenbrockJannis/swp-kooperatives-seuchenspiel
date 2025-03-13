package de.uol.swp.common.card.request;

import de.uol.swp.common.card.Card;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.player.Player;
import lombok.Getter;

/**
 * Represents a request to discard a specific card in the game.
 * <p>
 * This abstract request is used as a base class for more specific discard card requests
 * involving various types of cards. It encapsulates the game, player, and card information.
 * </p>
 *
 * @param <C> The type of card being discarded.
 */
public abstract class DiscardCardRequest<C extends Card> extends AbstractGameRequest {

    @Getter
    protected C card;

    /**
     * Constructs a new DiscardCardRequest.
     *
     * @param game   The game in which the card is being discarded.
     * @param player The player discarding the card.
     * @param card   The specific card to be discarded.
     */
    protected DiscardCardRequest(Game game, Player player, C card) {
        super(game, player);
        this.card = card;
    }
}
