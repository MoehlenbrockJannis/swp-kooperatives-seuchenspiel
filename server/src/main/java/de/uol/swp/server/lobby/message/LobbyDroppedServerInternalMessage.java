package de.uol.swp.server.lobby.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Message indicating that a lobby has been dropped.
 *
 * <p>This message is used internally by the server to notify that a lobby has been removed.</p>
 *
 * @since 2024-09-10
 */
@Getter
@AllArgsConstructor
public class LobbyDroppedServerInternalMessage extends AbstractServerInternalMessage {

    private final Lobby lobby;

}
