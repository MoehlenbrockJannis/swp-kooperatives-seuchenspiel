package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @see AbstractLobbyServerMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class LobbyJoinUserServerMessage extends AbstractLobbyServerMessage {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who joined the lobby
     * @since 2019-10-08
     */
    public LobbyJoinUserServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }
}
