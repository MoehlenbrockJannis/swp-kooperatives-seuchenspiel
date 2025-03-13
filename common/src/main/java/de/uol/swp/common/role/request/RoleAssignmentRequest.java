package de.uol.swp.common.role.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request to assign a role to a specific user within a lobby.
 * <p>
 * This request is sent by the client to assign a {@link RoleCard} to a {@link User} in a specific {@link Lobby}.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RoleAssignmentRequest extends AbstractRequestMessage {
    private final Lobby lobby;
    private final User user;
    private final RoleCard roleCard;
}
