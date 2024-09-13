package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Message sent by the server when a user successfully leaves a lobby
 *
 * @see AbstractLobbyServerMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class LobbyLeaveUserServerMessage extends AbstractLobbyServerMessage {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who left the lobby
     * @since 2019-10-08
     */
    public LobbyLeaveUserServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }
}
