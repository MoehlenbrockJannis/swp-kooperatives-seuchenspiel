package de.uol.swp.common.game.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import lombok.NoArgsConstructor;

/**
 * Request sent when a player leaves the game.
 * This will trigger server-side handling to mark the game as lost and end the current turn.
 */
@NoArgsConstructor
public class LeaveGameRequest extends AbstractGameRequest {

    /**
     * Constructor
     *
     * @param game The game being left
     * @param player The player who is leaving
     */
    public LeaveGameRequest(Game game, Player player) {
        super(game, player);
    }
}