package de.uol.swp.common.action.request;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The ActionRequest class represents a request message sent by a client
 * to perform a specific action in the game. It includes the game state
 * and the action to be executed.
 */

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ActionRequest extends AbstractGameRequest {
    private final Game game;
    private final Action action;
}
