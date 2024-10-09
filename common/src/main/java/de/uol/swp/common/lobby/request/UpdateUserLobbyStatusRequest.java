package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import lombok.Getter;

@Getter
public class UpdateUserLobbyStatusRequest extends AbstractLobbyRequest {
    private final LobbyStatus status;

    public UpdateUserLobbyStatusRequest(final Lobby lobby, final LobbyStatus status) {
        super(lobby);
        this.status = status;
    }
}
