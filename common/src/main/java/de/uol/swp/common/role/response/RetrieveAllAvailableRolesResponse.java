package de.uol.swp.common.role.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class RetrieveAllAvailableRolesResponse extends AbstractResponseMessage {

    private Set<RoleCard> roleCards;
    private Lobby lobby;
}
