package de.uol.swp.common.role.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server.AbstractServerMessage;
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
public class RetrieveAllAvailableRolesServerMessage extends AbstractServerMessage {
    private Lobby lobby;
    private Set<RoleCard> roleCards;
}
