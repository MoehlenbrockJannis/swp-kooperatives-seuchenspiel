package de.uol.swp.common.game.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Server message representing the creation of a new game.
 * <p>
 * This message is sent by the server to notify clients about the creation of a new game,
 * including the initialized {@link Game} object with all its relevant details.
 * </p>
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateGameServerMessage extends AbstractServerMessage {
    private Game game;
}
