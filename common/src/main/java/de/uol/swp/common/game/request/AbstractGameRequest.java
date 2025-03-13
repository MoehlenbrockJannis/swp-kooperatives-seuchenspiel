package de.uol.swp.common.game.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Base class for all game request messages. Handles basic game and player data.
 *
 * @see de.uol.swp.common.game.Game
 * @see de.uol.swp.common.player.Player
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public class AbstractGameRequest extends AbstractRequestMessage {
    protected Game game;
    protected Player player;
}
