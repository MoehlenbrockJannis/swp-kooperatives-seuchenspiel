package de.uol.swp.common.card.response;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.response.AbstractGameResponse;
import lombok.Getter;

@Getter
public class ReleaseToDiscardPlayerCardResponse extends AbstractGameResponse {

    private int numberOfCardsToDiscard;
    public ReleaseToDiscardPlayerCardResponse(Game game) {
        super(game);
        this.numberOfCardsToDiscard = 1;
    }
}
