package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Request sent to the server when a user wants to leave a lobby
 *
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class LobbyLeaveUserRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobby lobby
     * @param user user who wants to leave the lobby
     * @since 2019-10-08
     */
    public LobbyLeaveUserRequest(Lobby lobby, User user) {
        super(lobby, user);
    }

}
