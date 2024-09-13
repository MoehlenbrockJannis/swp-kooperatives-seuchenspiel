package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import lombok.Getter;

@Getter
public class LobbyUpdateStatusRequest extends AbstractLobbyRequest {
    private final LobbyStatus status;

    public LobbyUpdateStatusRequest(final Lobby lobby, final LobbyStatus status) {
        super(lobby, null);
        this.status = status;
    }
}
