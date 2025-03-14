package de.uol.swp.server.chat.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a system message directed to a specific lobby.
 * <p>
 * This internal server message is used to send system-generated messages, such as notifications or alerts,
 * to all users in a specific {@link Lobby}.
 * </p>
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class SystemLobbyMessageServerInternalMessage extends AbstractServerInternalMessage {

    private final String message;
    private final Lobby lobby;
}
