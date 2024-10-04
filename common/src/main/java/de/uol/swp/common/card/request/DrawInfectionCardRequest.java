package de.uol.swp.common.card.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.player.Player;

/**
 * A request to draw an infection card in the game.
 * This request is sent when a player needs to draw an infection card.
 */
public class DrawInfectionCardRequest extends AbstractGameRequest {

    /**
     * Constructs a new DrawInfectionCardRequest.
     *
     * @param game   the game in which the request is made
     * @param player the player making the request
     */
    private DrawInfectionCardRequest(Game game, Player player) {
        super(game, player);
    }
}
