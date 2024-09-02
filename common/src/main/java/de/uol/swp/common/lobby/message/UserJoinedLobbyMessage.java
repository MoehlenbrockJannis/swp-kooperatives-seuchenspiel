package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {
    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @param user user who joined the lobby
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
