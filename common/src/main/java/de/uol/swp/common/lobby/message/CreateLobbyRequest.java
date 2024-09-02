package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import lombok.NoArgsConstructor;

/**
 * Request sent to the server when a user wants to create a new lobby
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@NoArgsConstructor
public class CreateLobbyRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param name name of the lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public CreateLobbyRequest(String name, User owner) {
        super(name, owner);
    }

    /**
     * Setter for the user variable
     *
     * @param owner  User trying to create the lobby
     * @since 2019-10-08
     */
    public void setOwner(User owner) {
        setUser(owner);
    }

    /**
     * Getter for the user variable
     *
     * @return User trying to create the lobby
     * @since 2019-10-08
     */
    public User getOwner() {
        return getUser();
    }

}
