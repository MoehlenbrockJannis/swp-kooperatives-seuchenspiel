package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.*;

/**
 * Base class for all server messages related to a lobby.
 *
 * This class handles the basic structure for messages sent from the server regarding
 * a specific lobby. It extends {@link AbstractServerMessage} and contains a reference
 * to a {@link Lobby}, which is the lobby that the message pertains to.
 *
 * Subclasses of this message are used to notify clients about various lobby events,
 * such as users joining, leaving, or being kicked.
 *
 * @see AbstractServerMessage
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2024-10-06
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class AbstractLobbyServerMessage extends AbstractServerMessage {
    protected Lobby lobby;
}
