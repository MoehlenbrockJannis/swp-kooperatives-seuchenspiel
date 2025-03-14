package de.uol.swp.server.game.message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameEndReason;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Internal message sent when a game's state changes.
 */
@Getter
@AllArgsConstructor
public class GameStateChangedInternalMessage extends AbstractServerInternalMessage {
    private final Game game;
    private final GameEndReason reason;
}