package de.uol.swp.common.player.turn.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A server message to end the turn of a player
 *
 * @author Silas van Thiel
 * @since 2025-01-20
 */
@AllArgsConstructor
@Getter
public class EndPlayerTurnServerMessage extends AbstractServerMessage {
    private Game game;
}
