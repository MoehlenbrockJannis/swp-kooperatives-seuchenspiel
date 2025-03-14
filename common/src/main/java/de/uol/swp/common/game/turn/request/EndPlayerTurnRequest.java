package de.uol.swp.common.game.turn.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A request to end the turn of a player
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class EndPlayerTurnRequest extends AbstractRequestMessage {
    private final Game game;
}
