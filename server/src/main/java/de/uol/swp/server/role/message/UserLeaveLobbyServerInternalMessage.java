package de.uol.swp.server.role.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an internal server message to update for example all available roles when a player leaves the lobby.
 *
 * @author Jannis Moehlenbrock
 * @since 2024-10-02
 */
@Getter
@AllArgsConstructor
public class UserLeaveLobbyServerInternalMessage extends AbstractServerInternalMessage {

    private final Lobby lobby;
    private final User user;
}
