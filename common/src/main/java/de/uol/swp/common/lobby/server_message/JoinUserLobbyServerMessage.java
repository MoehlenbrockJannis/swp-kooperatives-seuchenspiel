package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @see AbstractUserLobbyServerMessage
 * @see de.uol.swp.common.user.User
 */
@NoArgsConstructor
public class JoinUserLobbyServerMessage extends AbstractUserLobbyServerMessage {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who joined the lobby
     */
    public JoinUserLobbyServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }
}
