package de.uol.swp.common.role.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
/**
 * This class receives all existing roles from the backend in the set when a ‘RolerenderAssignmentRequest’ request is executed.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RetrieveAllRolesResponse extends AbstractResponseMessage {
    private Set<RoleCard> roleCardSet = new HashSet<>();
    private Lobby lobby;

    public RetrieveAllRolesResponse(Set<RoleCard> roleCardSet, Lobby lobby) {
        this.roleCardSet.addAll(roleCardSet);
        this.lobby = lobby;
    }
}
