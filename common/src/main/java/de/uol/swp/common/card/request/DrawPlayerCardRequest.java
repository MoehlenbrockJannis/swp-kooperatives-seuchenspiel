package de.uol.swp.common.card.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.player.Player;

/**
 * A request to draw a player card in a game.
 */
public class DrawPlayerCardRequest extends AbstractGameRequest {


    /**
     * Constructs a new DrawPlayerCardRequest.
     *
     * @param game   the game in which the card is to be drawn
     * @param player the player who is drawing the card
     * @author Dominik Horn
     */
    public DrawPlayerCardRequest(Game game, Player player) {
        super(game, player);
    }

}
