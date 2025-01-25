package de.uol.swp.server.chat.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class SystemLobbyMessageServerInternalMessage extends AbstractServerInternalMessage {

    private final String message;
    private final Lobby lobby;
}
