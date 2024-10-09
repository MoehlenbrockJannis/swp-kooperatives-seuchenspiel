package de.uol.swp.common.role.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RetrieveAllAvailableRolesRequest extends AbstractRequestMessage {

    private Lobby lobby;
}
