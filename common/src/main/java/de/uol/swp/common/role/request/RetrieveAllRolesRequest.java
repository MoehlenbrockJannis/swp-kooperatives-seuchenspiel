package de.uol.swp.common.role.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request to retrieve all roles associated with a specific lobby.
 * <p>
 * This request is sent by the client to fetch all roles currently assigned within a given {@link Lobby}.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RetrieveAllRolesRequest extends AbstractRequestMessage {
    private Lobby lobby;
}
