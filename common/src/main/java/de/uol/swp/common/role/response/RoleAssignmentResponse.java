package de.uol.swp.common.role.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class RoleAssignmentResponse extends AbstractResponseMessage {
    private RoleCard roleCard;
    private boolean roleAssigned;
    private Lobby lobby;
}
