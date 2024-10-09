package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @see AbstractUserLobbyServerMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class JoinUserLobbyServerMessage extends AbstractUserLobbyServerMessage {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who joined the lobby
     * @since 2019-10-08
     */
    public JoinUserLobbyServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }
}
