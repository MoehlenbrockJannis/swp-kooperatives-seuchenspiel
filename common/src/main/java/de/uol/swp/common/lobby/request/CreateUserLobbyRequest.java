package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Request sent to the server when a user wants to create a new lobby
 *
 * @see AbstractUserLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class CreateUserLobbyRequest extends AbstractUserLobbyRequest {

    /**
     * Constructor
     *
     * @param lobby lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public CreateUserLobbyRequest(Lobby lobby, User owner) {
        super(lobby, owner);
    }
}
