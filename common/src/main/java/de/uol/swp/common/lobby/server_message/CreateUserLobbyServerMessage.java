package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

/**
 * Server message sent when a user creates or is added to a new user-specific lobby.
 * <p>
 * This message contains the {@link Lobby} information and the {@link User} who is associated with the lobby.
 * </p>
 */
public class CreateUserLobbyServerMessage extends AbstractUserLobbyServerMessage {

    public CreateUserLobbyServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }

}
