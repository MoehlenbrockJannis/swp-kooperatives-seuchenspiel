package de.uol.swp.common.role.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RoleAssignmentRequest extends AbstractRequestMessage {
    private final Lobby lobby;
    private final User user;
    private final RoleCard roleCard;
}
