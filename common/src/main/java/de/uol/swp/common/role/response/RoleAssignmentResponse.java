package de.uol.swp.common.role.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Response indicating the result of a role assignment request.
 * <p>
 * This response is sent by the server to inform whether a {@link RoleCard} was successfully assigned
 * to a user in a specific {@link Lobby}.
 * </p>
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class RoleAssignmentResponse extends AbstractResponseMessage {
    private RoleCard roleCard;
    private boolean roleAssigned;
    private Lobby lobby;
}
