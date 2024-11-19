package de.uol.swp.common.action.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The ActionServerMessage class represents a server message that includes
 * information about the state of the game after an action has been executed.
 * This message is sent to all clients to update them on the current game state.
 *
 * @Author: Jannis Moehlenbrock
 */

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class ActionServerMessage extends AbstractServerMessage {

    private Game game;

}
