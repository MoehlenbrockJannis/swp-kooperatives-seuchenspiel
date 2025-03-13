package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import lombok.Getter;

/**
 * Request to update the status of a lobby.
 * <p>
 * This request is used to change the {@link LobbyStatus} of a given {@link Lobby}.
 * </p>
 */
@Getter
public class UpdateLobbyStatusRequest extends AbstractLobbyRequest {

    /**
     * The new status to be applied to the lobby.
     */
    private final LobbyStatus status;

    /**
     * Constructs a new {@code UpdateLobbyStatusRequest}.
     *
     * @param lobby  The lobby whose status is to be updated.
     * @param status The new status for the lobby.
     */
    public UpdateLobbyStatusRequest(final Lobby lobby, final LobbyStatus status) {
        super(lobby);
        this.status = status;
    }
}