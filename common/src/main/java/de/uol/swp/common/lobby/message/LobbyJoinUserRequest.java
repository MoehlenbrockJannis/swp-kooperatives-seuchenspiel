package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Request sent to the server when a user wants to join a lobby
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class LobbyJoinUserRequest extends AbstractLobbyRequest {
    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @param user user who wants to join the lobby
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest(String lobbyName, User user) {
        super(lobbyName, user);
    }

}
