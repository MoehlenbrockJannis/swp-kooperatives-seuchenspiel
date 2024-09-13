package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Request sent to the server when a user wants to create a new lobby
 *
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class CreateLobbyRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobby lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public CreateLobbyRequest(Lobby lobby, User owner) {
        super(lobby, owner);
    }
}
