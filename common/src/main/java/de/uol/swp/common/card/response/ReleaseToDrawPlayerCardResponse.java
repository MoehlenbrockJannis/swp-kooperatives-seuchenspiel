package de.uol.swp.common.card.response;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.response.AbstractGameResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A message that is sent from the server to the client to release the player to draw a card
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class ReleaseToDrawPlayerCardResponse extends AbstractGameResponse {

    private final int numberOfPlayerCardsToDraw;

    /**
     * Constructor of the ReleaseToDrawPlayerCard
     * @param game the actual game
     */
    public ReleaseToDrawPlayerCardResponse(final Game game) {
        super(game);
        this.numberOfPlayerCardsToDraw = game.getCurrentTurn().getNumberOfPlayerCardsToDraw();
    }
}
