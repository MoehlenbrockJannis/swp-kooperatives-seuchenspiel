package de.uol.swp.server.role.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an internal server message to update for example all available roles when a player leaves the lobby.
 */
@Getter
@AllArgsConstructor
public class UserLeaveLobbyServerInternalMessage extends AbstractServerInternalMessage {
    private final Lobby lobby;
}
