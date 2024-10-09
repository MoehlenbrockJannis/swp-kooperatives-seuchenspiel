package de.uol.swp.common.card.request;


import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

/**
 * A request to discard an infection card in a game.
 * <p>
 * This class extends {@link DiscardCardRequest} and represents a request to discard a specific infection card
 * from a player's hand in a given game.
 * </p>
 */
public class DiscardInfectionCardRequest extends DiscardCardRequest<InfectionCard> {

    /**
     * Constructs a new DiscardInfectionCardRequest.
     *
     * @param game          The game from which the infection card is to be discarded
     * @param player        The player who is discarding the card
     * @param infectionCard The infection card to be discarded
     */
    public DiscardInfectionCardRequest(Game game, Player player, InfectionCard infectionCard) {
        super(game, player, infectionCard);
    }
}
