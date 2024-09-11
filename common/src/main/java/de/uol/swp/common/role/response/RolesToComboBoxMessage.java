package de.uol.swp.common.role.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
/**
 * This class receives all existing roles from the backend in the set when a ‘RolerenderAssignmentRequest’ request is executed.
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-07
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RolesToComboBoxMessage extends AbstractResponseMessage {

    private Set<RoleCard> roleCardSet = new HashSet<>();
    private Lobby lobby;

    public RolesToComboBoxMessage(Set<RoleCard> roleCardSet, Lobby lobby) {
        this.roleCardSet.addAll(roleCardSet);
        this.lobby = lobby;
    }
}
