package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server.AbstractServerMessage;
import de.uol.swp.common.user.User;
import lombok.*;

/**
 * Base class for all user-related lobby server messages.
 *
 * This class extends {@link AbstractLobbyServerMessage} and adds support for messages that involve
 * a specific {@link User} within a lobby. It is used in scenarios where server messages need to convey
 * information related to both the lobby and a user, such as when a user joins, leaves, or interacts
 * within the lobby.
 *
 * Subclasses should be created for specific user-related lobby messages.
 *
 * @see de.uol.swp.common.user.User
 * @see AbstractServerMessage
 * @since 2024-10-06
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
@Setter
public class AbstractUserLobbyServerMessage extends AbstractLobbyServerMessage {
    protected User user;

    /**
     * Constructs an {@code AbstractUserLobbyServerMessage} with the specified lobby and user.
     *
     * @param lobby The lobby that this server message is related to.
     * @param user  The user involved in the message.
     * @since 2024-10-06
     */
    public AbstractUserLobbyServerMessage(Lobby lobby, User user) {
        super(lobby);
        this.user = user;
    }
}
