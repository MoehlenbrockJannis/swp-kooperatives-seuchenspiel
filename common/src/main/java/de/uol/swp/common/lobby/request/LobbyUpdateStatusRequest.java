package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.message.AbstractLobbyRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LobbyUpdateStatusRequest extends AbstractLobbyRequest {
    private final Lobby lobby;
    private final LobbyStatus status;
}
