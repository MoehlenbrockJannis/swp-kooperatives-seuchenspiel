package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to join a lobby
 *
 * @see AbstractUserLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class JoinUserLobbyRequest extends AbstractUserLobbyRequest {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who wants to join the lobby
     * @since 2019-10-08
     */
    public JoinUserLobbyRequest(Lobby lobby, User user) {
        super(lobby, user);
    }

}
