package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.*;

/**
 * Represents a base class for all lobby request messages that involve a user.
 *
 * This class extends {@link AbstractLobbyRequest} and adds an additional {@link User} field,
 * representing the user involved in the lobby request. It serves as a common superclass for
 * request messages that require both lobby and user information, such as user join or leave
 * requests.
 *
 * @see de.uol.swp.common.user.User
 * @see AbstractRequestMessage
 * @since 2024-10-06
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AbstractUserLobbyRequest extends AbstractLobbyRequest {
    protected User user;

    /**
     * Constructs an {@code AbstractUserLobbyRequest} with the specified lobby and user.
     *
     * @param lobby The lobby that this request relates to.
     * @param user  The user involved in this lobby request.
     * @since 2024-10-06
     */
    public AbstractUserLobbyRequest(Lobby lobby, User user) {
        super(lobby);
        this.user = user;
    }
}
