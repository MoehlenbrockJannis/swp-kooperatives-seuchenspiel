package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to create a new lobby
 *
 * @see AbstractUserLobbyRequest
 * @see de.uol.swp.common.user.User
 */
public class CreateUserLobbyRequest extends AbstractUserLobbyRequest {

    /**
     * Constructor
     *
     * @param lobby lobby
     * @param owner User trying to create the lobby
     */
    public CreateUserLobbyRequest(Lobby lobby, User owner) {
        super(lobby, owner);
    }
}
