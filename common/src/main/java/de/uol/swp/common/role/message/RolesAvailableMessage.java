package de.uol.swp.common.role.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.AbstractServerMessage;
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
public class RolesAvailableMessage extends AbstractServerMessage {
    private Lobby lobby;
    private Set<RoleCard> roleCards;
}
