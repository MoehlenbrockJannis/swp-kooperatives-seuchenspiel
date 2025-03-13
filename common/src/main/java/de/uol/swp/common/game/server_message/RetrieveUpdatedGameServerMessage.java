package de.uol.swp.common.game.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Server message representing the retrieval of an updated game state.
 * <p>
 * This message is sent by the server to provide the clients with the latest {@link Game} object,
 * reflecting any updates or changes in the game state.
 * </p>
 */
@AllArgsConstructor
@Getter
public class RetrieveUpdatedGameServerMessage extends AbstractServerMessage {

    private Game game;
}
