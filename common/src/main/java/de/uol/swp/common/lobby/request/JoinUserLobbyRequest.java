package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to join a lobby
 *
 * @see AbstractUserLobbyRequest
 * @see de.uol.swp.common.user.User
 */
public class JoinUserLobbyRequest extends AbstractUserLobbyRequest {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who wants to join the lobby
     */
    public JoinUserLobbyRequest(Lobby lobby, User user) {
        super(lobby, user);
    }

}
