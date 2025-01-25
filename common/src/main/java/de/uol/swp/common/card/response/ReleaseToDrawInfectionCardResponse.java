package de.uol.swp.common.card.response;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.response.AbstractGameResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class ReleaseToDrawInfectionCardResponse extends AbstractGameResponse {

    private final int numberOfInfectionCardsToDraw;

    public ReleaseToDrawInfectionCardResponse(final Game game) {
        super(game);
        this.numberOfInfectionCardsToDraw = game.getCurrentTurn().getNumberOfInfectionCardsToDraw();
    }
}
