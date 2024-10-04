package de.uol.swp.common.card.request;

import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

/**
 * A request to discard a player card in a game.
 * <p>
 * This class extends {@link DiscardCardRequest} and represents a request to discard a specific player card
 * from a player's hand in a given game.
 * </p>
 *
 * @param <C> The type of player card being discarded
 */
public class DiscardPlayerCardRequest<C extends PlayerCard> extends DiscardCardRequest<PlayerCard> {

    /**
     * Constructs a new DiscardPlayerCardRequest.
     *
     * @param game      The game from which the player card is to be discarded
     * @param player    The player who is discarding the card
     * @param playerCard The player card to be discarded
     */
    public DiscardPlayerCardRequest(Game game, Player player, C playerCard) {
        super(game, player, playerCard);
    }
}
