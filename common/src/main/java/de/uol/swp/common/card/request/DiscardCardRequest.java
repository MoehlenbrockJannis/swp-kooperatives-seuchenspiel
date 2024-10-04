package de.uol.swp.common.card.request;

import de.uol.swp.common.card.Card;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.player.Player;
import lombok.Getter;

public abstract class DiscardCardRequest<C extends Card> extends AbstractGameRequest {

    @Getter
    protected C card;

    protected DiscardCardRequest(Game game, Player player, C card) {
        super(game, player);
        this.card = card;
    }
}
